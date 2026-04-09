package com.example.demo.feature.game.saveGame

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demo.domain.model.worldsave.WorldSave
import com.example.demo.domain.util.formatSaveTimestamp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

@Composable
fun SaveGameRoot(
    guildId: Long,
    backNav: () -> Unit,
    onSavedAs: (Long) -> Unit,
    gameScope: Scope
) {
    val viewModel : SaveGameViewModel = koinViewModel(
        scope = gameScope,
        parameters = { parametersOf(guildId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    SaveGameScreen(
        state = state,
        backNav = backNav,
        onSlotTapped = viewModel::onSlotTapped,
        onCreateNameChanged = viewModel::onCreateNameChanged,
        dismissDialogs = viewModel::dismissDialogs,
        confirmOverwrite = { slot ->
            viewModel.confirmOverwrite(slot = slot, onDone = onSavedAs)
        },
        confirmCreate = { slot ->
            viewModel.confirmCreate(slot = slot, onDone = onSavedAs)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveGameScreen(
    state: SaveGameState,
    backNav: () -> Unit,
    onSlotTapped: (Long) -> Unit,
    onCreateNameChanged: (String) -> Unit,
    dismissDialogs: () -> Unit,
    confirmOverwrite: (Long) -> Unit,
    confirmCreate: (Long) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Save As") },
                navigationIcon = {
                    IconButton(onClick = backNav) {
                        Text("<")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.slots) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = !state.isSaving
                        ) { onSlotTapped(row.slot) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text("Slot ${row.slot}", style = MaterialTheme.typography.titleMedium)
                        if (row.existingSave == null) {
                            Text("Empty slot", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            val existing = row.existingSave
                            Text(existing.characterName, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Last saved: ${formatSaveTimestamp(existing.lastSavedAt.times(1000))}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Playtime: ${existing.playtimeSeconds}s",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Text(
                        if (row.existingSave == null) "Create" else "Overwrite",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    val overwriteSlot = state.showOverwriteForSlot
    if (overwriteSlot != null) {
        AlertDialog(
            onDismissRequest = dismissDialogs,
            title = { Text("Overwrite Slot $overwriteSlot") },
            text = { Text("This will replace existing dave data in slot $overwriteSlot") },
            confirmButton = {
                TextButton(
                    onClick = { confirmOverwrite(overwriteSlot) },
                    enabled = !state.isSaving
                ) { Text("Overwrite") }
            },
            dismissButton = {
                TextButton(
                    onClick = dismissDialogs,
                    enabled = !state.isSaving
                ) { Text("Cancel") }
            }
        )
    }

    val createSlot = state.showCreateNameForSlot
    if (createSlot != null) {
        AlertDialog(
            onDismissRequest = dismissDialogs,
            title = { Text("Create Save in Slot $createSlot") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter character/save name")
                    TextField(
                        value = state.newNameInput,
                        onValueChange = onCreateNameChanged,
                        singleLine = true,
                        enabled = !state.isSaving
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { confirmCreate(createSlot) },
                    enabled = !state.isSaving && state.newNameInput.isNotBlank()
                ) { Text("Create & Save") }
            },
            dismissButton = {
                TextButton(
                    onClick = dismissDialogs,
                    enabled = !state.isSaving
                ) { Text("Cancel") }
            }
        )
    }
}