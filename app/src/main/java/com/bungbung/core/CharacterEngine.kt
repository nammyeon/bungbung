package com.bungbung.core

import kotlin.math.roundToInt

/** 붕붕이의 저장되는 상태. 실서비스에서 서버에 동기화하는 것도 이 값(요약)뿐이다. */
data class BungState(
    val personality: Int = 0,          // -100(겁 많음) ~ +100(다정하고 느긋함)
    val exp: Int = 0,
    val intimacy: Int = 30,            // 0 ~ 100
    val fullness: Int = 70,            // 0 ~ 100, 간식·주유로 오름
    val lastDriveDay: Long? = null,    // epoch day
    val lastPetDay: Long? = null,
    val petsToday: Int = 0,
    val collected: Set<String> = emptySet(),
    val equippedItem: String? = null,  // Region.id
    val totalKm: Double = 0.0,
    val lastTrip: TripSummary? = null,
    val bornDay: Long? = null,         // 처음 만난 날 (D+n 표시용)
) {
    val level: Int get() = CharacterEngine.levelFor(exp)
}

/** 차 상태에서 오는 컨디션. 여러 개면 위에 있을수록 우선. */
enum class Condition { SICK, SORE_FOOT, HUNGRY, NORMAL }

enum class Mood { HAPPY, CALM, SCARED, SULKY, HUNGRY, SICK, SLEEPY }

enum class PersonalityType(val label: String) {
    GENTLE("다정하고 느긋한"),
    MILD("온순한"),
    NORMAL("평범한"),
    SENSITIVE("예민한"),
    TIMID("겁 많은"),
}

/** 주행이 끝나고 주차했을 때 보여주는 '오늘의 성장 카드'. */
data class GrowthReport(
    val trip: TripSummary,
    val expGained: Int,
    val personalityDelta: Int,
    val intimacyDelta: Int,
    val leveledUp: Boolean,
    val newLevel: Int,
    val newItems: List<Region>,
    val line: String,
)

object CharacterEngine {
    const val EXP_PER_KM = 10
    const val SULKY_DAYS = 3
    const val LOW_FUEL_PERCENT = 15

    /** 레벨 n이 되려면 누적 경험치 50·n·(n-1)이 필요하다. (Lv2=100, Lv3=300, Lv4=600 …) */
    fun levelFor(exp: Int): Int {
        var level = 1
        while (exp >= 50 * (level + 1) * level) level++
        return level
    }

    fun expForLevel(level: Int): Int = 50 * level * (level - 1)

    fun personalityType(p: Int): PersonalityType = when {
        p >= 40 -> PersonalityType.GENTLE
        p >= 10 -> PersonalityType.MILD
        p > -10 -> PersonalityType.NORMAL
        p > -40 -> PersonalityType.SENSITIVE
        else -> PersonalityType.TIMID
    }

    fun condition(s: VehicleSnapshot?): Condition = when {
        s == null -> Condition.NORMAL
        s.warningOn -> Condition.SICK
        !s.tirePressureOk -> Condition.SORE_FOOT
        s.fuelPercent < LOW_FUEL_PERCENT -> Condition.HUNGRY
        else -> Condition.NORMAL
    }

    fun daysAway(state: BungState, today: Long): Int =
        state.lastDriveDay?.let { (today - it).toInt().coerceAtLeast(0) } ?: 0

    fun mood(state: BungState, condition: Condition, today: Long): Mood = when {
        condition == Condition.SICK || condition == Condition.SORE_FOOT -> Mood.SICK
        condition == Condition.HUNGRY || state.fullness < 20 -> Mood.HUNGRY
        daysAway(state, today) >= SULKY_DAYS -> Mood.SULKY
        personalityType(state.personality) == PersonalityType.TIMID -> Mood.SCARED
        state.lastTrip?.let { it.harshBrakes >= 2 } == true -> Mood.SCARED
        state.intimacy >= 60 || personalityType(state.personality) == PersonalityType.GENTLE -> Mood.HAPPY
        else -> Mood.CALM
    }

    /** 시동을 걸었을 때. 오래 안 탔으면 친밀도가 조금 떨어지고, 지난 운전을 기억해서 말을 건다. */
    fun onIgnition(state: BungState, condition: Condition, today: Long): Pair<BungState, String> {
        val away = daysAway(state, today)
        val decayed = if (away >= SULKY_DAYS) (state.intimacy - (away - 2) * 3).coerceAtLeast(0) else state.intimacy
        val next = state.copy(intimacy = decayed)
        val line = Dialogue.greeting(next, condition, away)
        return next to line
    }

    /** 주차했을 때. 주행 요약으로 경험치·성격·친밀도·도감을 반영한다. */
    fun onPark(state: BungState, trip: TripSummary, today: Long): Pair<BungState, GrowthReport> {
        val expGained = (trip.distanceKm * EXP_PER_KM).roundToInt()
        val personalityDelta = when {
            trip.distanceKm < 0.5 -> 0
            trip.isSmooth -> 4
            else -> -(trip.harshBrakes * 4 + trip.harshAccels * 3)
        }
        val firstToday = state.lastDriveDay != today
        val intimacyDelta = if (firstToday) 6 else 2
        val newExp = state.exp + expGained
        val next = state.copy(
            exp = newExp,
            personality = (state.personality + personalityDelta).coerceIn(-100, 100),
            intimacy = (state.intimacy + intimacyDelta).coerceAtMost(100),
            fullness = (state.fullness - (trip.distanceKm / 10).roundToInt()).coerceAtLeast(0),
            lastDriveDay = today,
            collected = state.collected + trip.newRegionIds,
            totalKm = state.totalKm + trip.distanceKm,
            lastTrip = trip,
        )
        val leveledUp = levelFor(newExp) > state.level
        val items = trip.newRegionIds.mapNotNull { Regions.byId(it) }
        val report = GrowthReport(
            trip = trip,
            expGained = expGained,
            personalityDelta = personalityDelta,
            intimacyDelta = intimacyDelta,
            leveledUp = leveledUp,
            newLevel = next.level,
            newItems = items,
            line = Dialogue.parked(trip, leveledUp, next.level),
        )
        return next to report
    }

    /** 간식 주기(주차 중 차 화면 또는 휴대폰). */
    fun giveSnack(state: BungState): Pair<BungState, String> {
        if (state.fullness >= 95) return state to "배불러서 더는 못 먹겠어~"
        return state.copy(fullness = (state.fullness + 20).coerceAtMost(100)) to "냠냠! 맛있다, 고마워!"
    }

    /** 실제 주유·충전 = 진짜 밥. */
    fun onRefuel(state: BungState): Pair<BungState, String> =
        state.copy(fullness = 100, intimacy = (state.intimacy + 3).coerceAtMost(100)) to "배불러! 이제 어디든 갈 수 있어!"

    /** 쓰다듬기. 하루 5번까지만 친밀도가 오른다. */
    fun pet(state: BungState, today: Long): Pair<BungState, String> {
        val count = if (state.lastPetDay == today) state.petsToday else 0
        if (count >= 5) return state.copy(lastPetDay = today, petsToday = count) to "헤헤, 오늘은 충분히 쓰다듬어 줬어!"
        val next = state.copy(
            intimacy = (state.intimacy + 1).coerceAtMost(100),
            lastPetDay = today,
            petsToday = count + 1,
        )
        return next to Dialogue.pet(personalityType(state.personality))
    }

    fun equip(state: BungState, regionId: String?): BungState =
        if (regionId == null || regionId in state.collected) state.copy(equippedItem = regionId) else state
}
