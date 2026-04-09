package com.example.demo.domain.di

import com.example.demo.domain.usecases.CreateNewCharacterMetaUseCase
import com.example.demo.domain.usecases.CreateNewGuildSaveFileUseCase
import com.example.demo.domain.usecases.DeleteGuildSaveFileUseCase
import com.example.demo.domain.usecases.GetGuildSaveFilesUseCase
import com.example.demo.domain.usecases.LoadGuildSaveFileUseCase
import com.example.demo.domain.usecases.OpenGuildSaveFileUseCase
import com.example.demo.domain.usecases.SaveGuildFileUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetGuildSaveFilesUseCase(get()) }
    factory { CreateNewGuildSaveFileUseCase(get()) }
    factory { OpenGuildSaveFileUseCase(get()) }
    factory { LoadGuildSaveFileUseCase(get()) }
    factory { SaveGuildFileUseCase(get(), get()) }
    factory { CreateNewCharacterMetaUseCase(get()) }
    factory { DeleteGuildSaveFileUseCase(get(), get()) }
}