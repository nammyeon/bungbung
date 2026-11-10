package com.bungbung.app

import com.bungbung.core.DriveScenario
import com.bungbung.core.ScenarioPlayer
import com.bungbung.core.VehicleSnapshot
import com.bungbung.core.VehicleSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 시연 모드의 차량 데이터. 시나리오의 1초 단위 샘플을 tickMs마다 하나씩 흘려보낸다.
 * 실제 차량에서는 같은 VehicleSource 모양으로 Pleos Vehicle SDK·FusedLocation SDK 값을 넣으면 된다.
 */
class SimulatedVehicleSource(
    private val scope: CoroutineScope,
    private val scenario: DriveScenario,
    private val tickMs: Long = 250L,
) : VehicleSource {
    private var job: Job? = null

    override fun start(onSample: (VehicleSnapshot) -> Unit, onFinished: () -> Unit) {
        job?.cancel()
        val samples = ScenarioPlayer.samples(scenario)
        job = scope.launch {
            for (s in samples) {
                onSample(s)
                delay(tickMs)
            }
            onFinished()
        }
    }

    override fun stop() {
        job?.cancel()
        job = null
    }
}
