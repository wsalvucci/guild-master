package com.example.demo

import android.app.Application
import com.example.demo.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GameApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GameApplication)
            modules(appModules())
        }
    }
}
