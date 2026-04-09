package com.example.demo

import androidx.compose.ui.window.ComposeUIViewController
import com.example.demo.di.appModules
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(appModules())
        }
    }
) { App() }