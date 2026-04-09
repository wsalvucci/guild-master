package com.example.demo.di

import android.content.Context
import com.example.demo.db.meta.MetaDatabaseDriverFactory
import com.example.demo.db.world.WorldDatabaseDriverFactory
import com.example.demo.data.theme.AndroidThemeStore
import com.example.demo.data.theme.PlatformThemeStore
import com.example.demo.db.world.WorldDatabaseFileManager
import org.koin.dsl.module

actual val platformModule = module {
    single { MetaDatabaseDriverFactory(get<Context>()) }
    single { WorldDatabaseDriverFactory(get<Context>()) }
    single { WorldDatabaseFileManager(get<Context>()) }
    single<PlatformThemeStore> { AndroidThemeStore(get<Context>()) }
}