package com.bungbung.app

import android.content.Context
import com.bungbung.core.BungState
import com.bungbung.core.TripSummary
import org.json.JSONArray
import org.json.JSONObject

/** 붕붕이 상태를 기기 안에만 저장한다. (운전 패턴·위치 원본은 저장하지 않고 요약만 남긴다) */
class BungStore(context: Context) {
    private val prefs = context.getSharedPreferences("bungbung", Context.MODE_PRIVATE)

    fun load(): BungState {
        val raw = prefs.getString(KEY_STATE, null) ?: return BungState()
        return runCatching { fromJson(JSONObject(raw)) }.getOrDefault(BungState())
    }

    fun save(state: BungState) {
        prefs.edit().putString(KEY_STATE, toJson(state).toString()).apply()
    }

    fun loadDayOffset(): Long = prefs.getLong(KEY_DAY_OFFSET, 0L)

    fun saveDayOffset(offset: Long) {
        prefs.edit().putLong(KEY_DAY_OFFSET, offset).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private fun toJson(s: BungState) = JSONObject().apply {
        put("personality", s.personality)
        put("exp", s.exp)
        put("intimacy", s.intimacy)
        put("fullness", s.fullness)
        s.lastDriveDay?.let { put("lastDriveDay", it) }
        s.lastPetDay?.let { put("lastPetDay", it) }
        put("petsToday", s.petsToday)
        put("collected", JSONArray(s.collected.toList()))
        s.equippedItem?.let { put("equippedItem", it) }
        put("totalKm", s.totalKm)
        s.bornDay?.let { put("bornDay", it) }
        s.lastTrip?.let { t ->
            put("lastTrip", JSONObject().apply {
                put("distanceKm", t.distanceKm)
                put("durationSec", t.durationSec)
                put("harshBrakes", t.harshBrakes)
                put("harshAccels", t.harshAccels)
                put("newRegionIds", JSONArray(t.newRegionIds))
            })
        }
    }

    private fun fromJson(o: JSONObject) = BungState(
        personality = o.optInt("personality", 0),
        exp = o.optInt("exp", 0),
        intimacy = o.optInt("intimacy", 30),
        fullness = o.optInt("fullness", 70),
        lastDriveDay = if (o.has("lastDriveDay")) o.getLong("lastDriveDay") else null,
        lastPetDay = if (o.has("lastPetDay")) o.getLong("lastPetDay") else null,
        petsToday = o.optInt("petsToday", 0),
        collected = o.optJSONArray("collected").toStringList().toSet(),
        equippedItem = if (o.has("equippedItem")) o.getString("equippedItem") else null,
        totalKm = o.optDouble("totalKm", 0.0),
        bornDay = if (o.has("bornDay")) o.getLong("bornDay") else null,
        lastTrip = o.optJSONObject("lastTrip")?.let { t ->
            TripSummary(
                distanceKm = t.optDouble("distanceKm", 0.0),
                durationSec = t.optDouble("durationSec", 0.0),
                harshBrakes = t.optInt("harshBrakes", 0),
                harshAccels = t.optInt("harshAccels", 0),
                newRegionIds = t.optJSONArray("newRegionIds").toStringList(),
            )
        },
    )

    private fun JSONArray?.toStringList(): List<String> =
        if (this == null) emptyList() else (0 until length()).map { getString(it) }

    private companion object {
        const val KEY_STATE = "state"
        const val KEY_DAY_OFFSET = "dayOffset"
    }
}
