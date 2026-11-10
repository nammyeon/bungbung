package com.bungbung.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bungbung.core.BungState
import com.bungbung.core.CharacterEngine
import com.bungbung.core.Condition
import com.bungbung.core.Dialogue
import com.bungbung.core.DriveScenario
import com.bungbung.core.DrivingAnalyzer
import com.bungbung.core.DrivingEventType
import com.bungbung.core.Gear
import com.bungbung.core.GrowthReport
import com.bungbung.core.Mood
import com.bungbung.core.RegionTracker
import com.bungbung.core.ScenarioPlayer
import com.bungbung.core.VehicleSnapshot
import com.bungbung.core.VehicleSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

/** 앱 화면 흐름: 시동 꺼짐 → 시동 인사 → 주행 중 → 주차(성장 카드·돌보기) */
enum class Phase { OFF, GREETING, DRIVING, PARKED }

data class UiState(
    val phase: Phase = Phase.OFF,
    val bung: BungState = BungState(),
    val scenario: DriveScenario = DriveScenario.all.first(),
    val vehicle: VehicleSnapshot? = null,
    val condition: Condition = Condition.NORMAL,
    val speech: String = "",
    val report: GrowthReport? = null,
    val driveLog: List<String> = emptyList(),
    val showDex: Boolean = false,
    val today: Long = 0L,
    val dayOffset: Long = 0L,
) {
    val mood: Mood get() = CharacterEngine.mood(bung, condition, today)
    val canTouch: Boolean get() = phase != Phase.DRIVING
}

class BungViewModel(app: Application) : AndroidViewModel(app) {
    private val store = BungStore(app)
    private val speaker = Speaker(app)
    private val notifier = Notifier(app)

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    private var source: VehicleSource? = null
    private var analyzer = DrivingAnalyzer()
    private var tracker = RegionTracker(emptySet())
    private var lastReactionSec = Double.NEGATIVE_INFINITY

    init {
        val offset = store.loadDayOffset()
        _ui.value = UiState(bung = store.load(), dayOffset = offset, today = today(offset))
    }

    private fun today(offset: Long) = LocalDate.now().toEpochDay() + offset

    private fun say(line: String) {
        _ui.update { it.copy(speech = line) }
        speaker.speak(line)
    }

    private fun updateBung(next: BungState) {
        store.save(next)
        _ui.update { it.copy(bung = next) }
    }

    // ── 시동 꺼짐 화면 ────────────────────────────

    fun selectScenario(id: String) {
        _ui.update { it.copy(scenario = DriveScenario.byId(id)) }
    }

    /** 시동 걸기: 차 상태로 컨디션을 정하고, 붕붕이가 먼저 말을 건다. */
    fun ignition() {
        val s = _ui.value
        val first = ScenarioPlayer.samples(s.scenario).first()
        val condition = CharacterEngine.condition(first)
        val (next, line) = CharacterEngine.onIgnition(s.bung, condition, s.today)
        updateBung(next)
        _ui.update { it.copy(phase = Phase.GREETING, vehicle = first, condition = condition, report = null, driveLog = emptyList()) }
        say(line)
    }

    /** 출발: 붕붕이는 화면에서 물러나고 백그라운드에서 운전을 기록한다. */
    fun depart() {
        val s = _ui.value
        analyzer = DrivingAnalyzer()
        tracker = RegionTracker(s.bung.collected)
        lastReactionSec = Double.NEGATIVE_INFINITY
        _ui.update { it.copy(phase = Phase.DRIVING, speech = "") }
        source = SimulatedVehicleSource(viewModelScope, s.scenario).also {
            it.start(onSample = ::onSample, onFinished = ::park)
        }
    }

    private fun onSample(v: VehicleSnapshot) {
        _ui.update { it.copy(vehicle = v) }
        analyzer.onSample(v)?.let { e ->
            val label = if (e.type == DrivingEventType.HARSH_BRAKE) "급제동" else "급가속"
            log("$label 감지 (${"%.0f".format(e.deltaKmhPerSec)} km/h/s)")
            // 주행 중 음성 반응은 짧게, 너무 자주 하지 않는다.
            if (v.timeSec - lastReactionSec >= 8) {
                lastReactionSec = v.timeSec
                say(if (e.type == DrivingEventType.HARSH_BRAKE) Dialogue.harshBrake() else Dialogue.harshAccel())
            }
        }
        tracker.onPosition(v.lat, v.lon)?.let { region ->
            log("${region.name} 진입 → ${region.itemEmoji} ${region.itemName}")
            notifier.popup("${region.name} 입성!", "${region.itemEmoji} ${region.itemName} 획득!", region.itemEmoji)
            say(Dialogue.newRegion(region))
        }
    }

    private fun log(line: String) {
        _ui.update { it.copy(driveLog = (it.driveLog + line).takeLast(6)) }
    }

    /** 주차(P단): 주행 요약으로 성장을 계산하고 '오늘의 성장 카드'를 보여준다. */
    fun park() {
        if (_ui.value.phase != Phase.DRIVING) return
        source?.stop()
        source = null
        val s = _ui.value
        val trip = analyzer.summary(tracker.newRegionIds())
        val (next, report) = CharacterEngine.onPark(s.bung, trip, s.today)
        updateBung(next)
        val parkedVehicle = s.vehicle?.copy(speedKmh = 0.0, gear = Gear.PARK)
        _ui.update { it.copy(phase = Phase.PARKED, report = report, vehicle = parkedVehicle) }
        if (report.leveledUp) notifier.popup("레벨 업!", "붕붕이가 Lv.${report.newLevel}이 됐어요", null)
        say(report.line)
    }

    fun turnOff() {
        source?.stop()
        source = null
        _ui.update { it.copy(phase = Phase.OFF, speech = "", showDex = false, vehicle = null, condition = Condition.NORMAL) }
    }

    // ── 주차 중 돌보기 (주행 중에는 화면에서 막는다) ────────────

    fun snack() {
        if (!_ui.value.canTouch) return
        val (next, line) = CharacterEngine.giveSnack(_ui.value.bung)
        updateBung(next)
        say(line)
    }

    fun pet() {
        if (!_ui.value.canTouch) return
        val (next, line) = CharacterEngine.pet(_ui.value.bung, _ui.value.today)
        updateBung(next)
        say(line)
    }

    /** 실제 주유·충전 = 진짜 밥. 시연에서는 버튼으로 주유를 흉내 낸다. */
    fun refuel() {
        if (!_ui.value.canTouch) return
        val (next, line) = CharacterEngine.onRefuel(_ui.value.bung)
        updateBung(next)
        _ui.update { s ->
            val v = s.vehicle?.copy(fuelPercent = 100)
            s.copy(vehicle = v, condition = CharacterEngine.condition(v))
        }
        say(line)
    }

    fun openDex(open: Boolean) {
        if (!_ui.value.canTouch) return
        _ui.update { it.copy(showDex = open) }
    }

    fun equip(regionId: String?) {
        updateBung(CharacterEngine.equip(_ui.value.bung, regionId))
    }

    // ── 시연용 도구 ────────────────────────────

    /** 며칠이 지난 것처럼 만든다(삐짐 시연용). */
    fun skipDays(days: Long) {
        val offset = _ui.value.dayOffset + days
        store.saveDayOffset(offset)
        _ui.update { it.copy(dayOffset = offset, today = today(offset)) }
    }

    fun resetAll() {
        source?.stop()
        store.clear()
        _ui.value = UiState(today = today(0))
    }

    override fun onCleared() {
        source?.stop()
        speaker.shutdown()
    }
}
