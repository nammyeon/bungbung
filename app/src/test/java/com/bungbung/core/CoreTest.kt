package com.bungbung.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreTest {

    private fun play(id: String, collected: Set<String> = emptySet()): TripSummary {
        val analyzer = DrivingAnalyzer()
        val tracker = RegionTracker(collected)
        ScenarioPlayer.samples(DriveScenario.byId(id)).forEach {
            analyzer.onSample(it)
            tracker.onPosition(it.lat, it.lon)
        }
        return analyzer.summary(tracker.newRegionIds())
    }

    @Test fun smoothDriveHasNoHarshEvents() {
        val trip = play("smooth")
        assertTrue(trip.isSmooth)
        assertTrue(trip.distanceKm > 1.0)
    }

    @Test fun harshDriveCountsEachEventOnce() {
        val trip = play("harsh")
        assertEquals(3, trip.harshBrakes)
        assertEquals(2, trip.harshAccels)
    }

    @Test fun gangneungTripCollectsRegionsInOrder() {
        val trip = play("gangneung")
        assertEquals(listOf("seoul", "hanam", "yangpyeong", "hoengseong", "pyeongchang", "gangneung"), trip.newRegionIds)
        assertTrue("거리 ${trip.distanceKm}", trip.distanceKm in 150.0..300.0)
    }

    @Test fun alreadyCollectedRegionsAreNotNew() {
        val trip = play("gangneung", setOf("seoul", "hanam"))
        assertEquals("yangpyeong", trip.newRegionIds.first())
    }

    @Test fun personalityFollowsDrivingStyle() {
        var s = BungState()
        repeat(3) { s = CharacterEngine.onPark(s, play("harsh"), today = 100L + it).first }
        assertTrue(s.personality < -40)
        assertEquals(PersonalityType.TIMID, CharacterEngine.personalityType(s.personality))

        var g = BungState()
        repeat(10) { g = CharacterEngine.onPark(g, play("smooth"), today = 100L + it).first }
        assertEquals(PersonalityType.GENTLE, CharacterEngine.personalityType(g.personality))
    }

    @Test fun levelsAndExp() {
        assertEquals(1, CharacterEngine.levelFor(0))
        assertEquals(2, CharacterEngine.levelFor(100))
        assertEquals(3, CharacterEngine.levelFor(300))
        val (s, report) = CharacterEngine.onPark(BungState(), play("gangneung"), today = 1L)
        assertTrue(report.leveledUp)
        assertEquals(6, s.collected.size)
    }

    @Test fun sulkyAfterThreeDays() {
        val s = BungState(lastDriveDay = 10L, intimacy = 50)
        assertEquals(Mood.SULKY, CharacterEngine.mood(s, Condition.NORMAL, today = 14L))
        val (next, line) = CharacterEngine.onIgnition(s, Condition.NORMAL, today = 14L)
        assertTrue(next.intimacy < 50)
        assertTrue(line.contains("4일"))
    }

    @Test fun vehicleConditionPriority() {
        val base = ScenarioPlayer.samples(DriveScenario.byId("smooth")).first()
        assertEquals(Condition.NORMAL, CharacterEngine.condition(base))
        assertEquals(Condition.HUNGRY, CharacterEngine.condition(base.copy(fuelPercent = 5)))
        assertEquals(Condition.SORE_FOOT, CharacterEngine.condition(base.copy(fuelPercent = 5, tirePressureOk = false)))
        assertEquals(Condition.SICK, CharacterEngine.condition(base.copy(tirePressureOk = false, warningOn = true)))
    }

    @Test fun scenarioStartsAndEndsParked() {
        val samples = ScenarioPlayer.samples(DriveScenario.byId("fuel"))
        assertEquals(Gear.PARK, samples.first().gear)
        assertEquals(Gear.PARK, samples.last().gear)
        assertEquals(0.0, samples.last().speedKmh, 0.0)
        assertTrue(samples.first().fuelPercent < CharacterEngine.LOW_FUEL_PERCENT)
    }

    @Test fun petLimitedPerDay() {
        var s = BungState(intimacy = 10)
        repeat(8) { s = CharacterEngine.pet(s, today = 5L).first }
        assertEquals(15, s.intimacy)
        s = CharacterEngine.pet(s, today = 6L).first
        assertEquals(16, s.intimacy)
    }
}
