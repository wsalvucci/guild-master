package com.example.demo.di

import com.example.demo.db.meta.MetaDatabase
import com.example.demo.db.meta.MetaDatabaseDriverFactory
import org.koin.dsl.module

val databaseModule = module {
    single { get<MetaDatabaseDriverFactory>().createDriver() }
    single { MetaDatabase(get()) }
    single { get<MetaDatabase>().guildQueries }
    single { get<MetaDatabase>().saveFileQueries }
}