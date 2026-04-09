package com.example.demo.domain.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.dsl.onClose

actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
actual fun createSimulationModule(): Module = module {
    single {
        val dispatcher = newSingleThreadContext("game-simulation")
        SimulationThread(
            dispatcher = dispatcher,
            onShutdown = { dispatcher.close() }
        )
    } onClose {
        it?.shutdown()
    }
}