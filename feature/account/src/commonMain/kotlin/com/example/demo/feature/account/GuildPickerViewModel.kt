package com.example.demo.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.domain.model.Guild
import com.example.demo.domain.repository.GuildRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GuildPickerUiState(
    val guilds: List<Guild> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = "",
    val createName: String = "",
    val isCreateOpen: Boolean = false,
)

class GuildPickerViewModel(
    private val repository: GuildRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GuildPickerUiState())
    val state: StateFlow<GuildPickerUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getAll() }
                .onSuccess { list ->
                    _state.update { it.copy(guilds = list, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error")}
                }
        }
    }

    fun openCreate(open: Boolean) {
        _state.update { it.copy(isCreateOpen = open, createName = if (!open) "" else it.createName) }
    }

    fun onCreateNameChange(value: String) {
        _state.update { it.copy(createName = value) }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun submitCreate() {
        val name = _state.value.createName.trim()
        if (name.isEmpty()) return

        viewModelScope.launch {
            runCatching { repository.insert(name) }
                .onSuccess {
                    openCreate(false)
                    refresh()
                }
                .onFailure { e ->
                    _state.update { it.copy(errorMessage = e.message ?: "Create failed") }
                }
        }
    }
}