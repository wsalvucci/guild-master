package com.example.demo.di

import com.example.demo.data.context.gameContextModule
import com.example.demo.data.di.repositoryModule
import com.example.demo.domain.di.useCaseModule
import com.example.demo.domain.util.createSimulationModule
import com.example.demo.feature.account.accountFeatureModule
import com.example.demo.feature.game.di.gameFeatureModule

fun appModules() = listOf(
    platformModule,
    databaseModule,
    repositoryModule,
    gameContextModule,
    accountFeatureModule,
    gameFeatureModule,
    useCaseModule,
    appUiModule,
    createSimulationModule()
)