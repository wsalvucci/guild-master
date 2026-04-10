package com.example.demo.feature.game.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demo.domain.model.tasks.Task
import com.example.demo.domain.model.tasks.TaskTemplate
import com.example.demo.domain.theme.AppThemeId
import com.example.demo.domain.theme.ThemeMode
import com.example.demo.domain.util.formatSaveTimestamp
import com.example.demo.feature.game.home.stats.StatsScreen
import com.example.demo.feature.game.home.tasks.WorkshopScreen
import com.example.demo.ui.theme.LocalThemeController
import com.example.demo.ui.theme.PreviewThemeController
import demo.feature.game.generated.resources.Home
import demo.feature.game.generated.resources.Res
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.scope.Scope

@Composable
fun GameHomeRoot(
    saveId: Long,
    backNav: () -> Unit,
    onSaveAsClicked: () -> Unit,
    gameScope: Scope
) {
    val viewModel: GameHomeViewModel = koinViewModel(
        scope = gameScope,
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    GameHomeScreen(
        state = state,
        backNav = backNav,
        onSaveAsClicked = onSaveAsClicked,
        onUpdateNewCharacterMetaName = viewModel::updateNewCharacterMetaName,
        onNewCharacterMetaCreated = viewModel::createNewCharacterMeta,
        toastMessageConsumed = viewModel::onToastMessageConsumed,
        onStartTask = viewModel::startTask,
        onCollectTask = viewModel::collectTask,
    )
}

data class PrimaryNavItem(
    val title: String,
    val icon: DrawableResource
)

val PRIMARY_NAV_MAP = mapOf(
    0 to PrimaryNavItem(
        title = "Stats",
        icon = Res.drawable.Home
    ),
    1 to PrimaryNavItem(
        title = "Workshop",
        icon = Res.drawable.Home
    ),
    2 to PrimaryNavItem(
        title = "Home",
        icon = Res.drawable.Home
    ),
    3 to PrimaryNavItem(
        title = "Guild",
        icon = Res.drawable.Home
    ),
    4 to PrimaryNavItem(
        title = "Settings",
        icon = Res.drawable.Home
    )
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameHomeScreen(
   state: GameHomeViewState,
   backNav: () -> Unit,
   onSaveAsClicked: () -> Unit,
   onUpdateNewCharacterMetaName: (String) -> Unit,
   onNewCharacterMetaCreated: () -> Unit,
   toastMessageConsumed: () -> Unit,
   onStartTask: (TaskTemplate) -> Unit,
   onCollectTask: (Task) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.toastMessage) {
        val message = state.toastMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        toastMessageConsumed()
    }

    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 5 }
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Save File: ${state.worldSave?.header?.saveId}")
                },
                navigationIcon = {
                    IconButton(onClick = backNav) {
                        Text("<")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .background(color = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(4.dp)
                        )
                        .background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(4.dp)
                        )
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PRIMARY_NAV_MAP.forEach { entry ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(entry.key)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            MainNavButton(
                                title = entry.value.title,
                                logo = entry.value.icon,
                            )
                        }
                        if (entry.key != PRIMARY_NAV_MAP.size - 1) {
                            VerticalDivider()
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> {
                    StatsScreen(
                        stats = state.worldSave?.characterData,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                1 -> {
                    WorkshopScreen(
                        activeTasks = state.worldSave?.activeTasks ?: emptyList(),
                        startTask = onStartTask,
                        collectTask = onCollectTask,
                        storageItems = state.worldSave?.characterData?.storage ?: emptyList(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                2 -> {
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Column {
                            Text("Game Home")
                            Text("Character Name: ${state.worldSave?.characterMeta?.name}")
                            Text("Last saved at: ${formatSaveTimestamp(state.worldSave?.header?.timestamp?.times(1000) ?: 0)}")
                            Text("Total playtime: ${state.worldSave?.header?.playtime}")
                        }
                    }
                }
                3 -> {}
                4 -> {
                    SettingsScreen(
                        onSaveAsClicked = onSaveAsClicked,
                        isSaving = state.isSaving,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }

        if (state.worldSave != null && state.worldSave.characterMeta == null) {
            CreateNewCharacterMetaScreen(
                state = state,
                submit = onNewCharacterMetaCreated,
                onUpdateNewCharacterMetaName = onUpdateNewCharacterMetaName,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewCharacterMetaScreen(
    state: GameHomeViewState,
    submit: () -> Unit,
    onUpdateNewCharacterMetaName: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text("Name your new character")
        },
        text = {
            TextField(
                value = state.newCharacterMeta.name,
                label = { Text("New Character Name") },
                onValueChange = onUpdateNewCharacterMetaName,
                singleLine = true,
            )
        },
        confirmButton = { TextButton(
            onClick = { submit() }
        ) { Text("Create") } },
        dismissButton = {}
    )
}

@Composable
private fun MainNavButton(
    title: String,
    logo: DrawableResource,
) {
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(logo),
                contentDescription = "Home Star Icon",
                modifier = Modifier.size(20.dp)
            )
            Text(text = title)
        }
    }
}


@Composable
private fun SettingsScreen(
    onSaveAsClicked: () -> Unit,
    isSaving: Boolean,
    modifier: Modifier = Modifier,
) {
    val theme = LocalThemeController.current
    val appModes = ThemeMode.entries
    val appThemes = AppThemeId.entries.chunked(3)
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = "Display",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
        )

        Text("Mode")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            appModes.forEach { mode ->
                AssistChip(
                    onClick = { theme.setMode(mode) },
                    label = { Text(
                        text = mode.name,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) },
                    colors = if (theme.prefs.mode == mode) {
                        AssistChipDefaults.assistChipColors().copy(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    } else {
                        AssistChipDefaults.assistChipColors()
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Text("Theme")
        LazyColumn {
            items(appThemes) { themeRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    themeRow.forEach { appTheme ->
                        AssistChip(
                            onClick = { theme.setTheme(appTheme) },
                            label = { Text(
                                text = appTheme.name,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) },
                            colors = if (theme.prefs.themeId == appTheme) {
                                AssistChipDefaults.assistChipColors().copy(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            } else {
                                AssistChipDefaults.assistChipColors()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 36.dp),
        )

        Text("Record Your Adventure")
        TextButton(
            onClick = onSaveAsClicked,
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp)
            ),
        ) {
            Text("Save Game")
        }
    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun GameHomeScreenPreview() {
    PreviewThemeController {
        GameHomeScreen(
            state = GameHomeViewState(),
            backNav = {},
            onSaveAsClicked = {},
            onUpdateNewCharacterMetaName = {},
            onNewCharacterMetaCreated = {},
            toastMessageConsumed = {},
            onStartTask = {},
            onCollectTask = {},
        )
    }
}