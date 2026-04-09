package com.example.demo.feature.account

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val accountFeatureModule = module {
    viewModelOf(::GuildPickerViewModel)
    viewModelOf(::GuildSaveFilePickerViewModel)
}