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

    private val _tickInterval = MutableStateFlow(1.seconds)
    val tickInterval = _tickInterval.asStateFlow()

    private var loopJob: Job? = null
    private var tickIndex: Long = 0L

    fun setTickInterval(tickInterval: Duration) {
        require(tickInterval.isPositive()) { "Tick interval must be positive" }
        _tickInterval.value = tickInterval
    }

    fun start(worldSave: WorldSave) {
        if (loopJob?.isActive == true) return

        loopJob = scope.launch {
            val mark = TimeSource.Monotonic.markNow()

            while (isActive) {
                val interval = _tickInterval.value
                val nextTick = tickIndex + 1
                val targetElapsed = interval.times(nextTick.toInt())
                val delayFor = targetElapsed - mark.elapsedNow()

                if (delayFor.isPositive()) delay(delayFor)

                tickIndex = nextTick

            }
        }
    }

    fun stop() {

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
}