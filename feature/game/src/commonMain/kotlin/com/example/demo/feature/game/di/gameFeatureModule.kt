package com.example.demo.feature.game.di

import com.example.demo.feature.game.MainGameViewModel
import com.example.demo.feature.game.home.GameHomeViewModel
import com.example.demo.feature.game.saveGame.SaveGameViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class GameSessionSource(
    val saveId: Long
)

val gameFeatureModule = module {
    scope<GameSessionScope> {
        scoped {
            val source = requireNotNull(getSource<GameSessionSource>()) {
                "Game session scope not found."
            }
            GameSessionStore(
                saveId = source.saveId,
                loadGuildSaveFileUseCase = get(),
                saveGuildFileUseCase = get(),
                createNewCharacterMetaUseCase = get(),
            )
        }

        viewModel {
            MainGameViewModel(
                session = get()
            )
        }

        viewModel {
            GameHomeViewModel(
                session = get()
            )
        }

        viewModel { (guildId: Long) ->
            SaveGameViewModel(
                guildId = guildId,
                getGuildSaveFilesUseCase = get(),
                createNewGuildSaveFileUseCase = get(),
                session = get()
            )
        }
    }
}

object GameSessionScope