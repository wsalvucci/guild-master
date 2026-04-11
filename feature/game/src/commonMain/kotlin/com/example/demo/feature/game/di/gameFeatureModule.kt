package com.example.demo.feature.game.di

import com.example.demo.core.simulation.SimulationEngine
import com.example.demo.domain.util.SimulationThread
import com.example.demo.feature.game.MainGameViewModel
import com.example.demo.feature.game.home.GameHomeViewModel
import com.example.demo.feature.game.home.tasks.WorkshopViewModel
import com.example.demo.feature.game.saveGame.SaveGameViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.onClose

data class GameSessionSource(
    val saveId: Long
)

val gameFeatureModule = module {
    scope<GameSessionScope> {
        scoped {
            val source = requireNotNull(getSource<GameSessionSource>()) {
                "Game session scope not found."
            }
            val thread = get<SimulationThread>()
            SimulationEngine(
                saveId = source.saveId,
                dispatcher = thread.dispatcher,
                loadGuildSaveFileUseCase = get(),
                saveGuildFileUseCase = get(),
                autoSaveGuildFileUseCase = get(),
            )
        } onClose {
            it?.stop()
        }

        viewModel {
            MainGameViewModel(
                simulation = get()
            )
        }

        viewModel {
            GameHomeViewModel(
                simulation = get()
            )
        }

        viewModel {
            WorkshopViewModel(
                simulationEngine = get()
            )
        }

        viewModel { (guildId: Long) ->
            SaveGameViewModel(
                guildId = guildId,
                getGuildSaveFilesUseCase = get(),
                createNewGuildSaveFileUseCase = get(),
                simulation = get()
            )
        }
    }
}

object GameSessionScope