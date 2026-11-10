package com.bungbung.core

/** 붕붕이의 대사. 성격·컨디션·지난 운전에 따라 고른다. */
object Dialogue {

    fun greeting(state: BungState, condition: Condition, daysAway: Int): String {
        when (condition) {
            Condition.SICK -> return "으… 몸 안에서 경고등이 켜졌어. 아파… 정비소에 데려가 줄래?"
            Condition.SORE_FOOT -> return "발이 좀 이상해… 치료가 필요해. (타이어 공기압을 확인해 주세요)"
            Condition.HUNGRY -> return "배고파… 꼬르륵. 주유소 들러 줄 거지?"
            Condition.NORMAL -> Unit
        }
        if (daysAway >= CharacterEngine.SULKY_DAYS) {
            return "흥, ${daysAway}일 동안 어디 갔었어? …그래도 와 줘서 좋아."
        }
        val trip = state.lastTrip
        if (trip != null) {
            if (trip.harshBrakes >= 2) return "어제 급정거 ${trip.harshBrakes}번… 나 좀 무서웠어. 오늘은 살살 운전해 줘!"
            if (trip.harshAccels >= 2) return "지난번엔 너무 쌩쌩 달렸어! 오늘은 천천히 가자~"
            if (trip.isSmooth && trip.distanceKm >= 1) return "지난번 운전 정말 부드러웠어. 오늘도 잘 부탁해!"
        }
        return when (CharacterEngine.personalityType(state.personality)) {
            PersonalityType.GENTLE -> "좋은 아침! 오늘도 느긋하게 같이 가 보자~"
            PersonalityType.MILD -> "안녕! 오늘은 어디 가?"
            PersonalityType.NORMAL -> "출발 준비 완료! 안전 운전하자!"
            PersonalityType.SENSITIVE -> "오늘은… 급정거 없는 거지? 조심조심 가자."
            PersonalityType.TIMID -> "히익… 오늘은 천천히 가 줄 거지? 약속해!"
        }
    }

    fun harshBrake(): String = "으악! 깜짝이야…"

    fun harshAccel(): String = "우왓, 너무 빨라!"

    fun newRegion(region: Region): String = "${region.name} 입성! ${region.itemName} 획득!"

    fun parked(trip: TripSummary, leveledUp: Boolean, level: Int): String = when {
        leveledUp -> "레벨 업! 나 Lv.$level 됐어!"
        trip.newRegionIds.isNotEmpty() -> "오늘 새로운 곳에 가서 신났어! 도감 확인해 봐!"
        trip.isSmooth && trip.distanceKm >= 1 -> "오늘 운전 최고였어. 하나도 안 무서웠어!"
        trip.harshBrakes >= 2 -> "도착… 휴, 오늘은 좀 무서웠어."
        else -> "도착! 오늘도 수고했어."
    }

    fun pet(type: PersonalityType): String = when (type) {
        PersonalityType.GENTLE, PersonalityType.MILD -> "헤헤, 기분 좋아~"
        PersonalityType.NORMAL -> "간지러워!"
        PersonalityType.SENSITIVE, PersonalityType.TIMID -> "…조금 괜찮아졌어."
    }
}
