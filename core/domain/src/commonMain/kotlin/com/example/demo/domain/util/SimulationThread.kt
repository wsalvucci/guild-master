package com.example.demo.domain.util

import kotlinx.coroutines.CoroutineDispatcher

class SimulationThread(
    val dispatcher: CoroutineDispatcher,
    val onShutdown: () -> Unit,
) {
    fun shutdown() = onShutdown()
}