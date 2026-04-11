package com.example.demo.feature.game.home.tasks

import androidx.lifecycle.ViewModel
import com.example.demo.core.simulation.SimulationEngine
import org.koin.compose.viewmodel.koinViewModel

class WorkshopViewModel(
    private val simulationEngine: SimulationEngine
) : ViewModel() {
    val activeTasks = simulationEngine.activeTasks
    val storageItems = simulationEngine.storageItems
}