package com.bungbung.core

/**
 * 시연용 주행 시나리오. 에뮬레이터에는 실제 주행이 없으니, 이 값으로 '가짜 주행'을 만든다.
 *
 * segments: (목표 속도 km/h, 그 속도로 바꾸는 가속도 km/h/s, 유지 시간 s)를 차례로 실행한다.
 * route: 주행하는 동안 지나가는 좌표. 진행률에 맞춰 선형으로 이동한다.
 * distanceScale: 시연 시간을 줄이기 위한 배율(강릉 여행 200km를 1분 안에 보여주기 위해).
 */
data class DriveScenario(
    val id: String,
    val title: String,
    val description: String,
    val segments: List<Segment>,
    val route: List<Pair<Double, Double>>,
    val distanceScale: Double = 1.0,
    val startFuelPercent: Int = 60,
    val tirePressureOk: Boolean = true,
    val warningOn: Boolean = false,
) {
    data class Segment(val targetKmh: Double, val rateKmhPerSec: Double, val holdSec: Double)

    companion object {
        private val SEOUL_COMMUTE = listOf(37.5665 to 126.9780, 37.5600 to 127.0100, 37.5120 to 127.0590)
        private val TO_GANGNEUNG = listOf(
            37.5665 to 126.9780, // 서울
            37.5393 to 127.2149, // 하남
            37.4917 to 127.4876, // 양평
            37.4918 to 127.9850, // 횡성
            37.3705 to 128.3900, // 평창
            37.7519 to 128.8761, // 강릉
        )

        private fun seg(target: Double, rate: Double, hold: Double) = Segment(target, rate, hold)

        val all: List<DriveScenario> = listOf(
            DriveScenario(
                id = "smooth",
                title = "출근길 · 부드러운 운전",
                description = "천천히 가속하고 미리 감속해요. 붕붕이 다정해져요.",
                segments = listOf(
                    seg(40.0, 3.0, 8.0), seg(60.0, 2.5, 10.0), seg(30.0, 3.0, 5.0),
                    seg(50.0, 2.5, 8.0), seg(0.0, 3.5, 2.0),
                ),
                route = SEOUL_COMMUTE,
                distanceScale = 6.0,
            ),
            DriveScenario(
                id = "harsh",
                title = "출근길 · 급정거 많음",
                description = "급가속·급정거를 반복해요. 붕붕이 겁을 먹어요.",
                segments = listOf(
                    seg(60.0, 14.0, 5.0), seg(10.0, 16.0, 2.0), seg(70.0, 13.0, 5.0),
                    seg(15.0, 18.0, 2.0), seg(55.0, 6.0, 5.0), seg(0.0, 15.0, 2.0),
                ),
                route = SEOUL_COMMUTE,
                distanceScale = 6.0,
            ),
            DriveScenario(
                id = "gangneung",
                title = "주말 강릉 여행",
                description = "서울에서 강릉까지. 지나가는 지역마다 도감 아이템을 얻어요.",
                segments = listOf(
                    seg(80.0, 3.0, 10.0), seg(100.0, 2.0, 30.0), seg(90.0, 2.0, 10.0),
                    seg(100.0, 2.0, 20.0), seg(40.0, 3.0, 5.0), seg(0.0, 3.0, 2.0),
                ),
                route = TO_GANGNEUNG,
                distanceScale = 58.0,
                startFuelPercent = 70,
            ),
            DriveScenario(
                id = "tire",
                title = "차량 관리 · 타이어 공기압 낮음",
                description = "시동을 걸면 붕붕이 발이 아프다고 해요.",
                segments = listOf(seg(40.0, 3.0, 8.0), seg(0.0, 3.0, 2.0)),
                route = SEOUL_COMMUTE,
                distanceScale = 6.0,
                tirePressureOk = false,
            ),
            DriveScenario(
                id = "fuel",
                title = "차량 관리 · 연료 부족",
                description = "붕붕이 배고파해요. 주차 후 '주유하기'로 진짜 밥을 줘요.",
                segments = listOf(seg(40.0, 3.0, 8.0), seg(0.0, 3.0, 2.0)),
                route = SEOUL_COMMUTE,
                distanceScale = 6.0,
                startFuelPercent = 9,
            ),
        )

        fun byId(id: String): DriveScenario = all.firstOrNull { it.id == id } ?: all.first()
    }
}

/**
 * 시나리오를 1초 간격의 차량 상태 목록으로 만든다. (순수 계산이라 테스트하기 쉽다)
 * 시동 직후 P → D로 바뀌고, 마지막에 다시 P가 된다.
 */
object ScenarioPlayer {
    fun samples(sc: DriveScenario, startOdometerKm: Double = 12_345.0): List<VehicleSnapshot> {
        val speeds = mutableListOf(0.0)
        var v = 0.0
        for (s in sc.segments) {
            while (v != s.targetKmh) {
                v = if (v < s.targetKmh) minOf(s.targetKmh, v + s.rateKmhPerSec) else maxOf(s.targetKmh, v - s.rateKmhPerSec)
                speeds += v
            }
            repeat(s.holdSec.toInt()) { speeds += v }
        }
        val n = speeds.size
        var odo = startOdometerKm
        var fuel = sc.startFuelPercent.toDouble()
        return speeds.mapIndexed { i, speed ->
            if (i > 0) {
                val km = (speeds[i - 1] + speed) / 2.0 / 3600.0 * sc.distanceScale
                odo += km
                fuel = (fuel - km / 12.0).coerceAtLeast(0.0)
            }
            val (lat, lon) = positionAt(sc.route, if (n <= 1) 1.0 else i.toDouble() / (n - 1))
            val gear = if (i == 0 || i == n - 1) Gear.PARK else Gear.DRIVE
            VehicleSnapshot(i.toDouble(), speed, gear, odo, fuel.toInt(), sc.tirePressureOk, sc.warningOn, lat, lon)
        }
    }

    fun positionAt(route: List<Pair<Double, Double>>, t: Double): Pair<Double, Double> {
        if (route.size == 1) return route[0]
        val x = t.coerceIn(0.0, 1.0) * (route.size - 1)
        val i = x.toInt().coerceAtMost(route.size - 2)
        val f = x - i
        val (a1, b1) = route[i]
        val (a2, b2) = route[i + 1]
        return (a1 + (a2 - a1) * f) to (b1 + (b2 - b1) * f)
    }
}
