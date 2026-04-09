package com.example.demo.di

import com.example.demo.ThemeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appUiModule = module {
    viewModelOf(::ThemeViewModel)
}