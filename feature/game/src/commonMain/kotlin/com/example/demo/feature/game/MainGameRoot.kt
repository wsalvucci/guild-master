package com.example.demo.feature.game

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.demo.feature.game.di.GameSessionScope
import com.example.demo.feature.game.di.GameSessionSource
import com.example.demo.feature.game.home.GameHomeRoot
import com.example.demo.feature.game.saveGame.SaveGameRoot
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainGameRoot(
    guildId: Long,
    saveId: Long,
    backNav: () -> Unit,
) {
    val koin = getKoin()
    val gameScope = remember(saveId) {
        koin.createScope(
            scopeId = "game-session-$saveId",
            qualifier = named<GameSessionScope>(),
            source = GameSessionSource(saveId)
        )
    }
    DisposableEffect(gameScope) {
        onDispose { gameScope.close() }
    }

    val viewModel: MainGameViewModel = koinViewModel(
        scope = gameScope
    )

    val gameNavController = rememberNavController()
    val sessionState by viewModel.state.collectAsStateWithLifecycle()

    when (sessionState) {
        is GameSessionState.Loading -> {}
        is GameSessionState.Ready -> {
            NavHost(
                navController = gameNavController,
                startDestination = GameRoute(guildId = guildId, saveId = saveId),
            ) {
                composable<GameRoute> { entry ->
                    val route = entry.toRoute<GameRoute>()
                    GameHomeRoot(
                        saveId = route.saveId,
                        backNav = backNav,
                        onSaveAsClicked = {
                            gameNavController.navigate(SaveListRoute(guildId = route.guildId))
                        },
                        gameScope = gameScope,
                    )
                }
                composable<SaveListRoute> { entry ->
                    SaveGameRoot(
                        guildId = guildId,
                        backNav = {
                            gameNavController.popBackStack()
                        },
                        onSavedAs = { newSaveId ->
                            gameNavController.popBackStack()
                        },
                        gameScope = gameScope,
                    )
                }
            }
        }
        is GameSessionState.Error -> {}
    }
}