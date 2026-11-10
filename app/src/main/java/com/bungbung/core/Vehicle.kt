package com.bungbung.core

/** 기어 상태. 실제 차량에서는 Vehicle SDK의 기어 값으로 채운다. */
enum class Gear { PARK, DRIVE, REVERSE, NEUTRAL }

/**
 * 어느 한 순간의 차량 상태.
 * 시연 모드에서는 DriveSimulator가, 실제 차량에서는 Pleos Vehicle/FusedLocation SDK가 이 값을 만든다.
 */
data class VehicleSnapshot(
    val timeSec: Double,
    val speedKmh: Double,
    val gear: Gear,
    val odometerKm: Double,
    val fuelPercent: Int,
    val tirePressureOk: Boolean,
    val warningOn: Boolean,
    val lat: Double,
    val lon: Double,
)

/** 차량 데이터를 흘려보내는 쪽. 시뮬레이터와 실제 SDK 연동부가 같은 모양을 갖도록 한다. */
interface VehicleSource {
    fun start(onSample: (VehicleSnapshot) -> Unit, onFinished: () -> Unit)
    fun stop()
}
