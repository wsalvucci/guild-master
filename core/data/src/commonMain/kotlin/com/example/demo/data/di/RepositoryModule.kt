package com.example.demo.data.di

import com.example.demo.data.WorldDatabaseManagerImpl
import com.example.demo.data.repository.GuildRepositoryImpl
import com.example.demo.data.repository.SaveSlotRepositoryImpl
import com.example.demo.data.repository.ThemePreferenceRepositoryImpl
import com.example.demo.data.repository.WorldSaveRepositoryImpl
import com.example.demo.db.world.WorldDatabaseManager
import com.example.demo.domain.repository.GuildRepository
import com.example.demo.domain.repository.SaveSlotRepository
import com.example.demo.domain.repository.ThemePreferenceRepository
import com.example.demo.domain.repository.WorldSaveRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<GuildRepository> { GuildRepositoryImpl(get()) }
    single<SaveSlotRepository> { SaveSlotRepositoryImpl(get()) }
    single<WorldSaveRepository> { WorldSaveRepositoryImpl(get(), get(), get()) }
    single<WorldDatabaseManager> { WorldDatabaseManagerImpl(get(), get()) }
    single<ThemePreferenceRepository> { ThemePreferenceRepositoryImpl(get()) }
}