package com.example.demo.feature.game.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.domain.model.tasks.TaskTemplate
import com.example.demo.domain.model.worldsave.CharacterMeta
import com.example.demo.domain.model.worldsave.WorldSave
import com.example.demo.domain.usecases.CreateNewCharacterMetaUseCase
import com.example.demo.domain.usecases.LoadGuildSaveFileUseCase
import com.example.demo.domain.usecases.SaveGuildFileUseCase
import com.example.demo.feature.game.di.GameSessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    private val session: GameSessionStore
) : ViewModel() {

    private var _state = MutableStateFlow(GameHomeViewState(isLoading = true))
    val state: StateFlow<GameHomeViewState> = _state

    init {
        viewModelScope.launch {
            runCatching { session.refresh() }
                .onFailure { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load save") }}
        }

        viewModelScope.launch {
            session.worldSave.collect { worldSave ->
                _state.update { it.copy(worldSave = worldSave) }
            }
        }
    }

    fun updateNewCharacterMetaName(name: String) {
        _state.update { it.copy(newCharacterMeta = it.newCharacterMeta.copy(name = name)) }
    }

    fun createNewCharacterMeta() {
        val newCharacterMeta = _state.value.newCharacterMeta
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            runCatching { session.createCharacter(newCharacterMeta.name) }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to create new character") }
                }

        }
    }

    fun onToastMessageConsumed() {
        _state.update { it.copy(toastMessage = null) }
    }

    fun startTask(template: TaskTemplate) {
        val newTask = template.instantiate()
        session.addActiveTask(newTask)
    }
}