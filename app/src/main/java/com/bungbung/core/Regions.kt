package com.bungbung.core

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** 지역 도감의 한 칸. 시연 버전은 중심 좌표 + 반경으로 지역을 판정한다(실서비스는 시·군·구 경계 데이터). */
data class Region(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val radiusKm: Double,
    val itemName: String,
    val itemEmoji: String,
)

object Regions {
    val all: List<Region> = listOf(
        Region("seoul", "서울", 37.5665, 126.9780, 14.0, "남산타워 열쇠고리", "🗼"),
        Region("hanam", "하남", 37.5393, 127.2149, 7.0, "스타필드 쇼핑백", "🛍️"),
        Region("yangpyeong", "양평", 37.4917, 127.4876, 12.0, "두물머리 연잎", "🪷"),
        Region("hoengseong", "횡성", 37.4918, 127.9850, 12.0, "한우 스테이크", "🥩"),
        Region("pyeongchang", "평창", 37.3705, 128.3900, 15.0, "스키 고글", "🥽"),
        Region("gangneung", "강릉", 37.7519, 128.8761, 12.0, "커피잔", "☕"),
        Region("sokcho", "속초", 38.2070, 128.5918, 8.0, "오징어순대", "🦑"),
        Region("chuncheon", "춘천", 37.8813, 127.7298, 10.0, "닭갈비 앞치마", "🍗"),
        Region("daejeon", "대전", 36.3504, 127.3845, 12.0, "빵 봉투", "🥐"),
        Region("jeonju", "전주", 35.8242, 127.1480, 10.0, "한옥 부채", "🪭"),
        Region("gyeongju", "경주", 35.8562, 129.2247, 12.0, "첨성대 배지", "🏛️"),
        Region("busan", "부산", 35.1796, 129.0756, 14.0, "갈매기 배지", "🐦"),
        Region("yeosu", "여수", 34.7604, 127.6622, 10.0, "밤바다 랜턴", "🏮"),
        Region("jeju", "제주", 33.4996, 126.5312, 20.0, "귤 모자", "🍊"),
    )

    fun byId(id: String): Region? = all.firstOrNull { it.id == id }

    /** 현재 좌표가 속한 지역. 여러 곳에 걸치면 가장 가까운 곳. */
    fun detect(lat: Double, lon: Double): Region? =
        all.map { it to distanceKm(lat, lon, it.lat, it.lon) }
            .filter { (r, d) -> d <= r.radiusKm }
            .minByOrNull { it.second }
            ?.first

    fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return 2 * r * asin(sqrt(a))
    }
}

/** 주행 중 처음 들어선 지역을 한 번씩만 알려준다. */
class RegionTracker(alreadyCollected: Set<String>) {
    private val collected = alreadyCollected.toMutableSet()
    private val newly = mutableListOf<String>()
    private var current: String? = null

    /** 새로 도감에 추가될 지역이면 반환, 아니면 null. */
    fun onPosition(lat: Double, lon: Double): Region? {
        val region = Regions.detect(lat, lon) ?: return null
        if (region.id == current) return null
        current = region.id
        if (!collected.add(region.id)) return null
        newly += region.id
        return region
    }

    fun newRegionIds(): List<String> = newly.toList()
}
