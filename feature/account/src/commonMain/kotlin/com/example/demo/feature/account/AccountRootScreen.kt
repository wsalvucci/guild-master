package com.example.demo.feature.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demo.domain.theme.AppThemeId
import com.example.demo.domain.theme.ThemeMode
import com.example.demo.domain.theme.ThemePreferences
import com.example.demo.ui.theme.LocalThemeController
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountRootScreen(
    onGuildSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GuildPickerViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    GuildPickerScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onOpenCreate = viewModel::openCreate,
        onCreateNameChange = viewModel::onCreateNameChange,
        onSubmitCreate = viewModel::submitCreate,
        onGuildSelected = onGuildSelected,
        clearError = viewModel::clearError,
        modifier = modifier,
    )
}

@Composable
fun GuildPickerScreen(
    state: GuildPickerUiState,
    onRefresh: () -> Unit,
    onOpenCreate: (Boolean) -> Unit,
    onCreateNameChange: (String) -> Unit,
    onSubmitCreate: () -> Unit,
    onGuildSelected: (Long) -> Unit,
    clearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalThemeController.current
    Scaffold(
        modifier = modifier,
        topBar = {
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onOpenCreate(true) }) {
                Text("+")
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            ThemeSettingsSection(
                selectedTheme = theme.prefs.themeId,
                selectedMode = theme.prefs.mode,
                onThemeSelected = theme.setTheme,
                onModeSelected = theme.setMode,
            )

            when {
                state.isLoading && state.guilds.isEmpty() -> CircularProgressIndicator()

                else -> LazyColumn {
                    items(state.guilds, key = { it.id }) { guild ->
                        ListItem(
                            headlineContent = { Text(guild.guildName) },
                            supportingContent = { Text("Guild #${guild.id}") },
                            modifier = Modifier.clickable { onGuildSelected(guild.id) }
                        )
                    }
                }
            }
        }

    }

    if (state.isCreateOpen) {
        AlertDialog(
            onDismissRequest = { onOpenCreate(false) },
            title = { Text("New guild") },
            text = {
                TextField(
                    value = state.createName,
                    onValueChange = onCreateNameChange,
                    label = { Text("Name") },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(onClick = onSubmitCreate) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { onOpenCreate(false) }) { Text("Cancel") }
            }
        )
    }

    state.errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { clearError() },
            confirmButton = { TextButton(onClick = { onRefresh() }) { Text("OK") } },
            title = { Text("Error") },
            text = { Text(msg) }
        )
    }
}

@Preview
@Composable
private fun AccountRootScreenPreview() {
    MaterialTheme {
        GuildPickerScreen(
            state = GuildPickerUiState(
                guilds = listOf(),
                isLoading = false,
                errorMessage = null,
            ),
            onRefresh = {},
            onOpenCreate = {},
            onCreateNameChange = {},
            onGuildSelected = {},
            clearError = {},
            onSubmitCreate = {},
        )
    }
}


// AUTOGENERATED TEST CODE
@Composable
fun ThemeSettingsSection(
    selectedTheme: AppThemeId,
    selectedMode: ThemeMode,
    onThemeSelected: (AppThemeId) -> Unit,
    onModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

        AssistChip(
            onClick = {
                when (selectedMode) {
                    ThemeMode.SYSTEM -> onModeSelected(ThemeMode.LIGHT)
                    ThemeMode.LIGHT -> onModeSelected(ThemeMode.DARK)
                    ThemeMode.DARK -> onModeSelected(ThemeMode.SYSTEM)
                }
            },
            label = { Text(selectedMode.name) },
        )

//        Text("Appearance", style = MaterialTheme.typography.titleMedium)
//
//        Text("Theme mode", style = MaterialTheme.typography.labelLarge)
//        ThemeMode.entries.forEach { mode ->
//            FilterChipRow(
//                label = mode.name,
//                selected = mode == selectedMode,
//                onClick = { onModeSelected(mode) }
//            )
//        }
//
//        Spacer(Modifier.height(8.dp))
//
//        Text("Color theme", style = MaterialTheme.typography.labelLarge)
//        AppThemeId.entries.forEach { theme ->
//            FilterChipRow(
//                label = theme.name,
//                selected = theme == selectedTheme,
//                onClick = { onThemeSelected(theme) }
//            )
//        }
    }
}

@Composable
private fun FilterChipRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (selected) {
            { Text("✓") }
        } else {
            null
        }
    )
}