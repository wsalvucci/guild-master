package com.example.demo.core.simulation

import com.example.demo.domain.model.worldsave.WorldSave
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class SimulationEngine(
    private val dispatcher: CoroutineDispatcher,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _tickInterval = MutableStateFlow(1.seconds)
    val tickInterval = _tickInterval.asStateFlow()

    private var loopJob: Job? = null
    private var tickIndex: Long = 0L

    fun setTickInterval(tickInterval: Duration) {
        require(tickInterval.isPositive()) { "Tick interval must be positive" }
        _tickInterval.value = tickInterval
    }

    fun start(worldSave: WorldSave) {
        if (loopJob?.isActive == true) return

        loopJob = scope.launch {
            val mark = TimeSource.Monotonic.markNow()

            while (isActive) {
                val interval = _tickInterval.value
                val nextTick = tickIndex + 1
                val targetElapsed = interval.times(nextTick.toInt())
                val delayFor = targetElapsed - mark.elapsedNow()

                if (delayFor.isPositive()) delay(delayFor)

                tickIndex = nextTick

            }
        }
    }

    fun stop() {

    }
}