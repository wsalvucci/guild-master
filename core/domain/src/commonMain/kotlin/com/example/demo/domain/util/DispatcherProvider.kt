package com.example.demo.domain.util

import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.Module

expect val ioDispatcher: CoroutineDispatcher

expect fun createSimulationModule(): Module