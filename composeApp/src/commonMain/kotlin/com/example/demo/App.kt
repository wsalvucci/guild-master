package com.example.demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.demo.domain.ActiveGuildContext
import com.example.demo.feature.account.AccountRootScreen
import com.example.demo.feature.account.GuildSaveFileRootScreen
import com.example.demo.feature.game.MainGameRoot
import com.example.demo.ui.theme.DemoTheme
import com.example.demo.ui.theme.LocalThemeController
import com.example.demo.ui.theme.ThemeController

import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val themeViewModel: ThemeViewModel = koinViewModel()
    val prefs by themeViewModel.prefs.collectAsStateWithLifecycle()

    DemoTheme(
        themeId = prefs.themeId,
        mode = prefs.mode
    ) {
        CompositionLocalProvider(
            LocalThemeController provides ThemeController(
                prefs = prefs,
                setTheme = { themeViewModel.setTheme(it) },
                setMode = { themeViewModel.setMode(it) },
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = GuildPickerRoute
                ) {
                    composable<GuildPickerRoute> {
                        AccountRootScreen(
                            onGuildSelected = { guildId ->
                                navController.navigate(SavePickerRoute(guildId = guildId)) {
                                    popUpTo<GuildPickerRoute> { }
                                }
                            }
                        )
                    }
                    composable<SavePickerRoute> { entry ->
                        val route = entry.toRoute<SavePickerRoute>()
                        val activeGuild: ActiveGuildContext = koinInject()
                        LaunchedEffect(route.guildId) {
                            activeGuild.setGuildId(route.guildId)
                        }
                        GuildSaveFileRootScreen(
                            guildId = route.guildId,
                            backNav = {
                                navController.popBackStack()
                            },
                            onSaveFileSelected = { saveId ->
                                navController.navigate(MainGameRoute(guildId = route.guildId, saveId = saveId))
                            }
                        )
                    }
                    composable<MainGameRoute> { entry ->
                        val route = entry.toRoute<MainGameRoute>()

                        MainGameRoot(
                            guildId = route.guildId,
                            saveId = route.saveId,
                            backNav = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}