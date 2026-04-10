package com.example.demo.core.simulation

import com.example.demo.domain.model.tasks.Task
import com.example.demo.domain.model.worldsave.CharacterMeta
import com.example.demo.domain.model.worldsave.WorldSave
import com.example.demo.domain.usecases.LoadGuildSaveFileUseCase
import com.example.demo.domain.usecases.SaveGuildFileUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class SimulationEngine(
    private val saveId: Long,
    private val dispatcher: CoroutineDispatcher,
    private val loadGuildSaveFileUseCase: LoadGuildSaveFileUseCase,
    private val saveGuildFileUseCase: SaveGuildFileUseCase,
) {
    // WORLD SAVE STATE
    private val _worldSave = MutableStateFlow<WorldSave?>(null)
    val worldSave = _worldSave.asStateFlow()

    suspend fun refreshFromDb() {
        _worldSave.value = loadGuildSaveFileUseCase(saveId)
    }

    suspend fun saveAs(targetSaveId: Long) {
        val current = _worldSave.value ?: return
        val withTimestamps = current.copy(
            header = current.header.copy(
                saveId = targetSaveId,
                timestamp = Clock.System.now().epochSeconds
            )
        )
        saveGuildFileUseCase(
            worldSave = withTimestamps,
            targetSaveId = targetSaveId,
        )
    }

    suspend fun autoSave() {
        // TODO: autoSaveGuildFileUseCase()
    }

    // SIMULATION ENGINE
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private var loopJob: Job? = null
    private var simTickRate: Duration = 17.milliseconds

    fun start() {
        if (loopJob?.isActive == true) return

        loopJob = scope.launch {
            val mark = TimeSource.Monotonic.markNow()
            var last = mark.elapsedNow()

            while (isActive) {
                val now = mark.elapsedNow()
                val diff = now - last
                if (diff >= simTickRate) {
                    simActiveTasks(simDelta = diff)
                    last = now
                } else {
                    delay(simTickRate - diff)
                }
            }
        }
    }

    fun stop() {
        loopJob?.cancel()
    }


    fun updateCharacterMeta(characterMeta: CharacterMeta) {
        _worldSave.update { cur ->
            if (cur == null) return@update null
            cur.copy(
                characterMeta = characterMeta
            )
        }
    }

    fun addActiveTask(task: Task) {
        _worldSave.update { current ->
            if (current == null) return@update null
            current.copy(
                activeTasks = current.activeTasks.plus(task),
            )
        }
    }

    fun collectTask(task: Task) {
        _worldSave.update { current ->
            if (current == null) return@update null
            current.copy(
                activeTasks = current.activeTasks.filter { it.uuid != task.uuid },
                characterData = current.characterData.copy(
                    storage = current.characterData.storage.plus(
                        task.outputItems.flatMap { outItem -> List(outItem.quantity) { outItem.item } }
                    )
                )
            )
        }
    }

    private val activeTaskInterval = 1.seconds
    private fun simActiveTasks(
        simDelta: Duration
    ) {
        val activeTasks = _worldSave.value?.activeTasks ?: return
        val runningTasks = activeTasks.filter { task ->
            true // TODO: Filter on tasks w/ active characters working on them
        }

        val notRunningTasks = activeTasks.filter { task ->
            false // TODO: Filter on tasks w/o active characters working on them
        }

        val updatedRunning = runningTasks.map { task ->
            val increment = task.workPerSecond.times(simDelta.div(activeTaskInterval))
            task.copy(workCompleted = minOf(task.workCompleted + increment, task.totalWork))
        }

        _worldSave.update { current ->
            current?.copy(
                activeTasks = updatedRunning.plus(notRunningTasks)
            )
        }
    }
}