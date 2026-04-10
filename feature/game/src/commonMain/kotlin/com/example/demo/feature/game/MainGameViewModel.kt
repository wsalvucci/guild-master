package com.example.demo.feature.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.core.simulation.SimulationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface GameSessionState {
    data object Loading : GameSessionState
    data object Ready : GameSessionState
    data class Error(val message: String) : GameSessionState
}
class MainGameViewModel(
    val simulation: SimulationEngine
) : ViewModel() {

    private val _state = MutableStateFlow<GameSessionState>(GameSessionState.Loading)
    val state: StateFlow<GameSessionState> = _state

    init {
        viewModelScope.launch {
            runCatching { simulation.refreshFromDb() }
                .onSuccess {
                    _state.update { GameSessionState.Ready }
                    simulation.start()
                }
                .onFailure { e -> _state.update { GameSessionState.Error(e.message ?: "") } }
        }
    }
}