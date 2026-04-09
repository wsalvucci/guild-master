package com.example.demo.feature.game.di

import com.example.demo.domain.model.tasks.Task
import com.example.demo.domain.model.worldsave.WorldSave
import com.example.demo.domain.usecases.CreateNewCharacterMetaUseCase
import com.example.demo.domain.usecases.LoadGuildSaveFileUseCase
import com.example.demo.domain.usecases.SaveGuildFileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock

class GameSessionStore(
    private val saveId: Long,
    private val loadGuildSaveFileUseCase: LoadGuildSaveFileUseCase,
    private val saveGuildFileUseCase: SaveGuildFileUseCase,
    private val createNewCharacterMetaUseCase: CreateNewCharacterMetaUseCase
) {
    private val _worldSave = MutableStateFlow<WorldSave?>(null)
    val worldSave: StateFlow<WorldSave?> = _worldSave
    suspend fun refresh() {
        _worldSave.value = loadGuildSaveFileUseCase(saveId)
    }
    suspend fun createCharacter(name: String) {
        createNewCharacterMetaUseCase(name)
        // repository writes to currently-open save; reload authoritative state
        refresh()
    }
    suspend fun saveAs(targetSaveId: Long) {
        val current = _worldSave.value ?: return
        val withTimestamps = current.copy(
            header = current.header.copy(
                saveId = targetSaveId,
                timestamp = Clock.System.now().epochSeconds
            )
        )
        saveGuildFileUseCase(worldSave = withTimestamps, targetSaveId = targetSaveId)
    }

    suspend fun close() {

    }

    fun addActiveTask(task: Task) {
        _worldSave.update { current ->
            if (current == null) return@update null
            current.copy(
                characterData = current.characterData.copy(
                    activeTasks = current.characterData.activeTasks.plus(task)
                )
            )
        }
    }
}