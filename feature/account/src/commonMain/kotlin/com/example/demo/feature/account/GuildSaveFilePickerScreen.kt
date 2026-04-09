package com.example.demo.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demo.domain.model.WorldSaveSlot
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.collections.forEach
import kotlin.math.roundToInt

@Composable
fun GuildSaveFileRootScreen(
    guildId: Long,
    backNav: () -> Unit,
    onSaveFileSelected: (Long) -> Unit,
    viewModel: GuildSaveFilePickerViewModel = koinViewModel(
        parameters = { parametersOf(guildId) }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    GuildSaveFilePickerScreen(
        state = state,
        backNav = backNav,
        onSaveFileSelected = onSaveFileSelected,
        onOpenCreate = viewModel::openCreate,
        onCreateFileNameChange = viewModel::onCreateNewFileNameChange,
        submitFileCreate = viewModel::submitNewFile,
        onDeleteGuildSaveFile = viewModel::onDelete,
        errorMessageConsumed = viewModel::errorMessageConsumed
    )
}

enum class AnchoredDragState {
    Open,
    Closed,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuildSaveFilePickerScreen(
    state: GuildSaveFilePickerState,
    backNav: () -> Unit,
    onSaveFileSelected: (Long) -> Unit,
    onOpenCreate: (Boolean) -> Unit,
    onCreateFileNameChange: (String) -> Unit,
    submitFileCreate: (Long) -> Unit,
    onDeleteGuildSaveFile: (Long) -> Unit,
    errorMessageConsumed: () -> Unit,
) {
    val density = LocalDensity.current
    val openPx = with(density) { -100.dp.toPx() }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        errorMessageConsumed()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Load a Save")
                },
                navigationIcon = {
                    IconButton(onClick = backNav) {
                        Text("<")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onOpenCreate(true) } ) {
                Text("+")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(state.saveFiles.size) { index ->
                val file = state.saveFiles[index]
                val rowDragState = remember(file.saveId) {
                    AnchoredDraggableState(
                        anchors = DraggableAnchors {
                            AnchoredDragState.Open at openPx
                            AnchoredDragState.Closed at 0f
                        },
                        initialValue = AnchoredDragState.Closed
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(2.dp, Color.Black)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.errorContainer),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { onDeleteGuildSaveFile(file.saveId) }) { Text("Delete") }
                    }
                    Row(
                        modifier = Modifier
                            .offset { IntOffset(rowDragState.requireOffset().roundToInt(), 0) }
                            .anchoredDraggable(
                                state = rowDragState,
                                orientation = Orientation.Horizontal
                            )
                            .background(MaterialTheme.colorScheme.background)
                            .fillMaxSize()
                            .clickable(
                                onClick = { onSaveFileSelected(file.saveId) }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "${file.saveId} - ${file.accountId} - ${file.characterName} - ${file.worldDbFile}",
                        )
                    }
                }
            }
        }
    }

    if (state.isCreateNewFileOpen) {
        AlertDialog(
            onDismissRequest = { onOpenCreate(false) },
            title = { Text("New Save File") },
            text = {
                TextField(
                    value = state.newFileName,
                    onValueChange = onCreateFileNameChange,
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    runCatching { submitFileCreate((state.saveFiles.size + 1).toLong()) }
                        .onSuccess { onOpenCreate(false) }
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { onOpenCreate(false) }) { Text("Cancel") }
            }
        )
    }
}

@Preview
@Composable
private fun GuildSaveFilePickerScreenPreview() {
    MaterialTheme {
        GuildSaveFilePickerScreen(
            state = GuildSaveFilePickerState(
                saveFiles = listOf(
                    WorldSaveSlot(
                        saveId = 1,
                        accountId = 1,
                        slot = 1,
                        characterName = "Character 1",
                        worldDbFile = "world1.db",
                        playtimeSeconds = 1234,
                        lastSavedAt = 1234
                    )
                )
            ),
            backNav = {},
            onSaveFileSelected = {},
            onCreateFileNameChange = {},
            submitFileCreate = {},
            onOpenCreate = {},
            onDeleteGuildSaveFile = {},
            errorMessageConsumed = {}
        )
    }
}