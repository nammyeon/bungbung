package com.bungbung.core

enum class DrivingEventType { HARSH_BRAKE, HARSH_ACCEL }

data class DrivingEvent(val type: DrivingEventType, val timeSec: Double, val deltaKmhPerSec: Double)

/** 한 번의 주행(시동 ~ 주차)을 요약한 결과. 캐릭터 엔진이 이걸로 성장을 계산한다. */
data class TripSummary(
    val distanceKm: Double,
    val durationSec: Double,
    val harshBrakes: Int,
    val harshAccels: Int,
    val newRegionIds: List<String>,
) {
    val isSmooth: Boolean get() = harshBrakes == 0 && harshAccels == 0
}

/**
 * 속도 변화량으로 급제동·급가속을 판단한다.
 *
 * 기준: 1초 동안 속도가 HARSH_BRAKE_KMH_PER_SEC 이상 줄면 급제동,
 * HARSH_ACCEL_KMH_PER_SEC 이상 늘면 급가속. 급제동이 몇 초 이어져도 한 번으로 센다.
 */
class DrivingAnalyzer(
    private val harshBrakeKmhPerSec: Double = HARSH_BRAKE_KMH_PER_SEC,
    private val harshAccelKmhPerSec: Double = HARSH_ACCEL_KMH_PER_SEC,
) {
    private var prev: VehicleSnapshot? = null
    private var first: VehicleSnapshot? = null
    private var ongoing: DrivingEventType? = null
    private var brakes = 0
    private var accels = 0

    fun onSample(s: VehicleSnapshot): DrivingEvent? {
        if (first == null) first = s
        val p = prev
        prev = s
        if (p == null) return null
        val dt = s.timeSec - p.timeSec
        if (dt <= 0.0) return null
        val rate = (s.speedKmh - p.speedKmh) / dt
        val type = when {
            rate <= -harshBrakeKmhPerSec && p.speedKmh >= MIN_SPEED_KMH -> DrivingEventType.HARSH_BRAKE
            rate >= harshAccelKmhPerSec -> DrivingEventType.HARSH_ACCEL
            else -> null
        }
        val isNew = type != null && type != ongoing
        ongoing = type
        if (!isNew) return null
        if (type == DrivingEventType.HARSH_BRAKE) brakes++ else accels++
        return DrivingEvent(type!!, s.timeSec, rate)
    }

    fun summary(newRegionIds: List<String>): TripSummary {
        val a = first
        val b = prev
        return TripSummary(
            distanceKm = if (a != null && b != null) (b.odometerKm - a.odometerKm).coerceAtLeast(0.0) else 0.0,
            durationSec = if (a != null && b != null) b.timeSec - a.timeSec else 0.0,
            harshBrakes = brakes,
            harshAccels = accels,
            newRegionIds = newRegionIds,
        )
    }

    companion object {
        const val HARSH_BRAKE_KMH_PER_SEC = 9.0
        const val HARSH_ACCEL_KMH_PER_SEC = 11.0
        const val MIN_SPEED_KMH = 15.0
    }
}
