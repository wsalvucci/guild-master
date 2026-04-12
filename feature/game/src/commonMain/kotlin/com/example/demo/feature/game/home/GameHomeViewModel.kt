package com.example.demo.feature.game.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.core.simulation.SimulationEngine
import com.example.demo.domain.model.tasks.Task
import com.example.demo.domain.model.tasks.TaskTemplate
import com.example.demo.domain.model.worldsave.CharacterMeta
import com.example.demo.domain.model.worldsave.WorldSave
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class GameHomeViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val worldSave: WorldSave? = null,
    val isSaving: Boolean = false,
    val toastMessage: String? = null,
    val newCharacterMeta: NewCharacterMetaViewState = NewCharacterMetaViewState(),
)

data class NewCharacterMetaViewState(
    val id: Long? = null,
    val name: String = ""
)
class GameHomeViewModel(
    private val simulation: SimulationEngine
) : ViewModel() {

    private var _state = MutableStateFlow(GameHomeViewState(isLoading = true))
    val state: StateFlow<GameHomeViewState> = _state

    init {
        viewModelScope.launch {
            simulation.worldSave.collect { worldSave ->
                _state.update { it.copy(worldSave = worldSave) }
            }
        }
    }

    fun updateNewCharacterMetaName(name: String) {
        _state.update { it.copy(newCharacterMeta = it.newCharacterMeta.copy(name = name)) }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun createNewCharacterMeta() {
        viewModelScope.launch {
            val newCharacterMeta = _state.value.newCharacterMeta
            val uuid = Uuid.random().toString()
            _state.update {
                it.copy(isLoading = true)
            }
            runCatching { simulation.updateCharacterMeta(CharacterMeta(characterUuid = uuid, name = newCharacterMeta.name)) }
                .onSuccess { _state.update { it.copy(isLoading = false, errorMessage = null) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to create new character") } }
        }
    }

    fun onToastMessageConsumed() {
        _state.update { it.copy(toastMessage = null) }
    }

    fun playerStartTask(template: TaskTemplate) {
        val characterId = _state.value.worldSave?.characterMeta?.characterUuid ?: return
        if (_state.value.worldSave == null) return
        startTask(template, characterId)
    }

    fun startTask(template: TaskTemplate, characterId: String) {
        val newTask = template.instantiate(
            initiatingCharacterId = characterId,
        )
        simulation.addActiveTask(newTask)
    }

    fun collectTask(task: Task) {
        viewModelScope.launch {
            simulation.collectTask(task)
        }
    }
}