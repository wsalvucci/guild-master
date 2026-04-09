package com.example.demo.feature.game.saveGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.domain.model.worldsave.WorldSave
import com.example.demo.domain.model.WorldSaveSlot
import com.example.demo.domain.repository.SaveSlotRepository
import com.example.demo.domain.usecases.CreateNewGuildSaveFileUseCase
import com.example.demo.domain.usecases.GetGuildSaveFilesUseCase
import com.example.demo.domain.usecases.SaveGuildFileUseCase
import com.example.demo.feature.game.di.GameSessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

private const val MAX_SLOTS = 20

data class SaveSlotUi(
    val slot: Long,
    val existingSave: WorldSaveSlot? = null,
)

data class SaveGameState(
    val slots: List<SaveSlotUi> = emptyList(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val showOverwriteForSlot: Long? = null,
    val showCreateNameForSlot: Long? = null,
    val newNameInput: String = ""
)

class SaveGameViewModel(
    private val guildId: Long,
    private val getGuildSaveFilesUseCase: GetGuildSaveFilesUseCase,
    private val createNewGuildSaveFileUseCase: CreateNewGuildSaveFileUseCase,
    private val session: GameSessionStore
) : ViewModel() {

    private val _state = MutableStateFlow(SaveGameState())
    val state: StateFlow<SaveGameState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            runCatching { getGuildSaveFilesUseCase(guildId) }
                .onSuccess { saveFiles ->
                    val bySlot = saveFiles.associateBy { it.slot }
                    val slotRows = (1..MAX_SLOTS).map { slot ->
                        SaveSlotUi(slot = slot.toLong(), existingSave = bySlot[slot])
                    }
                    _state.update { it.copy(slots = slotRows, errorMessage = null) }
                }
                .onFailure { e ->
                    _state.update { it.copy(errorMessage = e.message ?: "Failed to load slots") }
                }
        }
    }

    fun onSlotTapped(slot: Long) {
        val row = _state.value.slots.firstOrNull { it.slot == slot } ?: return

        if (row.existingSave == null) {
            _state.update { it.copy(showCreateNameForSlot = slot, newNameInput = "") }
        } else {
            _state.update { it.copy(showOverwriteForSlot = slot) }
        }
    }

    fun onCreateNameChanged(name: String) {
        _state.update { it.copy(newNameInput = name) }
    }

    fun dismissDialogs() {
        _state.update { it.copy(showOverwriteForSlot = null, showCreateNameForSlot = null, newNameInput = "") }
    }

    fun confirmOverwrite(
        slot: Long,
        onDone: (Long) -> Unit = {}
    ) {
        val target = _state.value.slots.firstOrNull { it.slot == slot }?.existingSave ?: return
        viewModelScope.launch {
            runCatching { session.saveAs(target.saveId) }
                .onSuccess { onDone(target.saveId) }
        }
    }

    fun confirmCreate(
        slot: Long,
        onDone: (Long) -> Unit = {}
    ) {
        val name = _state.value.newNameInput.trim()
        if (name.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            runCatching { createNewGuildSaveFileUseCase(
                accountId = guildId,
                slot = slot,
                guildName = name
            ) }
                .onSuccess { newSave ->
                    runCatching { session.saveAs(newSave.saveId) }
                        .onSuccess { onDone(newSave.saveId) }
                        .onFailure { e -> _state.update { it.copy(errorMessage = e.message ?: "Failed to create file") } }
                }
                .onFailure { e ->
                    _state.update { it.copy(errorMessage = e.message ?: "Failed to create save slot") }
                }
        }
    }

    private fun saveToTarget(
        targetSaveId: Long,
        onDone: (Long) -> Unit,
    ) {
        viewModelScope.launch {
            runCatching { session.saveAs(targetSaveId) }
                .onSuccess {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            showOverwriteForSlot = null,
                            showCreateNameForSlot = null,
                            newNameInput = "",
                        )
                    }
                    refresh()
                    onDone(targetSaveId)
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = e.message ?: "Failed to save"
                        )
                    }
                }
        }
    }

}