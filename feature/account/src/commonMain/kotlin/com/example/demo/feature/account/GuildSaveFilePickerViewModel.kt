package com.example.demo.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.domain.model.WorldSaveSlot
import com.example.demo.domain.usecases.CreateNewGuildSaveFileUseCase
import com.example.demo.domain.usecases.DeleteGuildSaveFileUseCase
import com.example.demo.domain.usecases.GetGuildSaveFilesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GuildSaveFilePickerState(
    val saveFiles: List<WorldSaveSlot>,
    val isLoading: Boolean = false,
    val isCreateNewFileOpen: Boolean = false,
    val newFileName: String = "",
    val errorMessage: String? = null
)

class GuildSaveFilePickerViewModel(
    private val guildId: Long,
    private val getGuildSaveFilesUseCase: GetGuildSaveFilesUseCase,
    private val createNewGuildSaveFileUseCase: CreateNewGuildSaveFileUseCase,
    private val deleteGuildSaveFileUseCase: DeleteGuildSaveFileUseCase
) : ViewModel() {

    private var _state = MutableStateFlow(GuildSaveFilePickerState(saveFiles = emptyList()))
    val state = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            runCatching { getGuildSaveFilesUseCase(guildId = guildId) }
                .onSuccess { saveFiles ->
                    _state.update { it.copy(saveFiles = saveFiles) }
                }
        }
    }

    fun openCreate(open: Boolean) {
        _state.update { it.copy(isCreateNewFileOpen = open) }
    }

    fun onCreateNewFileNameChange(name: String) {
        _state.update { it.copy(newFileName = name) }
    }

    fun submitNewFile(slot: Long) {
        val name = _state.value.newFileName.trim()
        if (name.isEmpty()) return

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            runCatching { createNewGuildSaveFileUseCase(
                accountId = guildId,
                slot = slot,
                guildName = name
            ) }
                .onSuccess { worldSaveSlot ->
                    _state.update {
                        it.copy(isLoading = false, saveFiles = it.saveFiles + worldSaveSlot)
                    }
                    refresh()
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "") }
                }
        }
    }

    fun onDelete(saveId: Long) {
        viewModelScope.launch {
            runCatching { deleteGuildSaveFileUseCase(saveId) }
                .onSuccess { refresh() }
                .onFailure { e -> _state.update { it.copy(errorMessage = e.message ?: "") } }
        }
    }

    fun errorMessageConsumed() {
        _state.update { it.copy(errorMessage = null) }
    }
}