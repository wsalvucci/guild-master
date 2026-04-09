package com.example.demo.di

import com.example.demo.db.meta.MetaDatabaseDriverFactory
import com.example.demo.db.world.WorldDatabaseDriverFactory
import com.example.demo.data.theme.IosThemeStore
import com.example.demo.data.theme.PlatformThemeStore
import com.example.demo.db.world.WorldDatabaseFileManager
import org.koin.dsl.module

actual val platformModule = module {
    single { MetaDatabaseDriverFactory() }
    single { WorldDatabaseDriverFactory() }
    single { WorldDatabaseFileManager() }
    single<PlatformThemeStore> { IosThemeStore() }
}