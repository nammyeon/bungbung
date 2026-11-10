package com.bungbung.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.bungbung.app.BungViewModel
import com.bungbung.app.Phase
import com.bungbung.app.UiState
import com.bungbung.core.CharacterEngine
import com.bungbung.core.DriveScenario
import com.bungbung.core.GrowthReport
import com.bungbung.core.Mood
import com.bungbung.core.Regions

/** 액정에 띄우는 화면 */
private enum class LcdPage { HOME, SCENARIOS, STATUS, DEX, REPORT }

/** 다마고치처럼 액정 위 4개, 아래 4개 메뉴 아이콘 */
private enum class Menu(val sprite: Sprite, val label: String) {
    FOOD(PixelArt.iconFood, "간식"),
    PET(PixelArt.iconPet, "쓰다듬기"),
    DEX(PixelArt.iconDex, "도감"),
    STATUS(PixelArt.iconStatus, "상태"),
    KEY(PixelArt.iconKey, "시동"),
    FUEL(PixelArt.iconFuel, "주유"),
    CLOCK(PixelArt.iconClock, "3일 뒤로"),
    RESET(PixelArt.iconReset, "처음부터"),
}

private fun keyLabel(phase: Phase) = when (phase) {
    Phase.OFF -> "시동 걸기"
    Phase.GREETING -> "출발"
    Phase.DRIVING -> "주행 중"
    Phase.PARKED -> "시동 끄기"
}

/** 시연용 도구(시간 건너뛰기·초기화)는 시동이 꺼져 있을 때만 쓸 수 있다. */
private fun enabled(m: Menu, phase: Phase) = when (m) {
    Menu.CLOCK, Menu.RESET -> phase == Phase.OFF
    else -> phase != Phase.DRIVING
}

@Composable
fun BungApp(state: UiState, vm: BungViewModel) {
    var page by remember { mutableStateOf(LcdPage.HOME) }
    var cursor by remember { mutableIntStateOf(-1) }     // 선택된 메뉴 아이콘(-1: 없음)
    var index by remember { mutableIntStateOf(0) }       // 목록·도감·카드 안에서의 위치

    LaunchedEffect(state.phase) {
        page = if (state.phase == Phase.PARKED && state.report != null) LcdPage.REPORT else LcdPage.HOME
        index = 0
        cursor = -1
    }

    fun run(m: Menu) {
        if (!enabled(m, state.phase)) return
        when (m) {
            Menu.FOOD -> { page = LcdPage.HOME; vm.snack() }
            Menu.PET -> { page = LcdPage.HOME; vm.pet() }
            Menu.DEX -> { page = LcdPage.DEX; index = 0 }
            Menu.STATUS -> { page = LcdPage.STATUS; index = 0 }
            Menu.KEY -> when (state.phase) {
                Phase.OFF -> {
                    page = LcdPage.SCENARIOS
                    index = DriveScenario.all.indexOf(state.scenario).coerceAtLeast(0)
                }
                Phase.GREETING -> vm.depart()
                Phase.PARKED -> vm.turnOff()
                Phase.DRIVING -> Unit
            }
            Menu.FUEL -> { page = LcdPage.HOME; vm.refuel() }
            Menu.CLOCK -> vm.skipDays(3)
            Menu.RESET -> { vm.resetAll(); page = LcdPage.HOME }
        }
    }

    val reportPages = state.report?.let { reportPages(it) } ?: emptyList()
    val menus = Menu.entries

    // 버튼: A 고르기 / B 확인 / C 취소 (주행 중에는 C만 '주차' 시연용)
    val onA: () -> Unit = {
        when {
            state.phase == Phase.DRIVING -> Unit
            page == LcdPage.HOME -> {
                var next = cursor
                for (k in menus.indices) {
                    next = (next + 1) % menus.size
                    if (enabled(menus[next], state.phase)) break
                }
                cursor = next
            }
            page == LcdPage.SCENARIOS -> index = (index + 1) % DriveScenario.all.size
            page == LcdPage.DEX -> index = (index + 1) % Regions.all.size
            page == LcdPage.STATUS -> index = (index + 1) % 2
            page == LcdPage.REPORT -> index = (index + 1).coerceAtMost(reportPages.size - 1)
        }
        Unit
    }
    val onB: () -> Unit = {
        when {
            state.phase == Phase.DRIVING -> Unit
            page == LcdPage.HOME -> if (cursor >= 0) run(menus[cursor])
            page == LcdPage.SCENARIOS -> {
                vm.selectScenario(DriveScenario.all[index].id)
                vm.ignition()
            }
            page == LcdPage.DEX -> {
                val r = Regions.all[index]
                if (r.id in state.bung.collected) vm.equip(if (state.bung.equippedItem == r.id) null else r.id)
            }
            page == LcdPage.STATUS -> index = (index + 1) % 2
            page == LcdPage.REPORT -> if (index < reportPages.size - 1) index++ else page = LcdPage.HOME
        }
        Unit
    }
    val onC: () -> Unit = {
        when {
            state.phase == Phase.DRIVING -> vm.park()
            page == LcdPage.HOME -> cursor = -1
            else -> { page = LcdPage.HOME; index = 0 }
        }
        Unit
    }
    val labels = when {
        state.phase == Phase.DRIVING -> ButtonLabels("", "", "주차")
        page == LcdPage.HOME -> ButtonLabels("고르기", "확인", "취소")
        page == LcdPage.DEX -> ButtonLabels("다음", "착용", "닫기")
        page == LcdPage.REPORT -> ButtonLabels("다음", "다음", "닫기")
        else -> ButtonLabels("다음", "확인", "닫기")
    }

    TamaDevice(labels, onA, onB, onC) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val dot = maxHeight / 92
            if (state.phase == Phase.DRIVING) {
                DrivingLcd(state, dot)
            } else {
                Column(Modifier.fillMaxSize().padding(dot * 2)) {
                    MenuRow(menus.take(4), menus, cursor, state.phase, dot) { m -> cursor = menus.indexOf(m); run(m) }
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        when (page) {
                            LcdPage.HOME -> HomeLcd(state, dot, cursor.takeIf { it >= 0 }?.let { menus[it] })
                            LcdPage.SCENARIOS -> ScenarioLcd(index, dot) { i ->
                                if (i == index) {
                                    vm.selectScenario(DriveScenario.all[i].id)
                                    vm.ignition()
                                } else {
                                    index = i
                                }
                            }
                            LcdPage.STATUS -> StatusLcd(state, index, dot)
                            LcdPage.DEX -> DexLcd(state, index, dot) { i ->
                                if (i == index) {
                                    val r = Regions.all[i]
                                    if (r.id in state.bung.collected) vm.equip(if (state.bung.equippedItem == r.id) null else r.id)
                                } else {
                                    index = i
                                }
                            }
                            LcdPage.REPORT -> ReportLcd(reportPages, index, dot)
                        }
                    }
                    MenuRow(menus.drop(4), menus, cursor, state.phase, dot) { m -> cursor = menus.indexOf(m); run(m) }
                }
            }
        }
    }
}

@Composable
private fun MenuRow(row: List<Menu>, all: List<Menu>, cursor: Int, phase: Phase, dot: Dp, onTap: (Menu) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        row.forEach { m ->
            val on = enabled(m, phase)
            val selected = all.indexOf(m) == cursor
            PixelIcon(
                m.sprite,
                dot * 1.1f,
                modifier = Modifier
                    .clickable(enabled = on) { onTap(m) }
                    .padding(dot),
                ink = if (selected) Ink else if (on) InkLight else InkGhost,
                light = if (selected) InkLight else InkGhost,
            )
        }
    }
}

@Composable
private fun LcdText(text: String, dot: Dp, bold: Boolean = false, scale: Float = 1f, maxLines: Int = 2, modifier: Modifier = Modifier) {
    Text(
        text,
        style = pixelText((dot * 5f * scale).asSp(), bold),
        textAlign = TextAlign.Center,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

// ── 기본 화면: 붕붕이 + 말풍선 ────────────────────────

@Composable
private fun HomeLcd(state: UiState, dot: Dp, selected: Menu?) {
    val mood = if (state.phase == Phase.OFF && state.speech.isBlank()) Mood.SLEEPY else state.mood
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val hint = when {
            selected == Menu.KEY -> "▶ ${keyLabel(state.phase)}"
            selected != null -> "▶ ${selected.label}"
            state.phase == Phase.GREETING -> "열쇠를 누르면 출발!"
            state.phase == Phase.OFF -> "열쇠를 눌러 시동을 걸어요"
            else -> ""
        }
        LcdText(hint, dot, scale = 0.8f, maxLines = 1)
        PixelBung(mood, state.bung.equippedItem, dot * 2.1f)
        val line = state.speech.ifBlank { if (state.phase == Phase.OFF) "쿨쿨…" else "" }
        LcdText(line, dot, modifier = Modifier.padding(horizontal = dot * 2))
    }
}

// ── 시동 걸기 전: 시연 시나리오 고르기 ─────────────────

@Composable
private fun ScenarioLcd(index: Int, dot: Dp, onTap: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = dot * 3)) {
        LcdText("어디로 갈까?", dot, bold = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(dot * 2))
        DriveScenario.all.forEachIndexed { i, sc ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onTap(i) }
                    .padding(vertical = dot * 0.8f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.width(dot * 6)) {
                    if (i == index) PixelIcon(PixelArt.cursor, dot)
                }
                Text(sc.title, style = pixelText((dot * 3.8f).asSp(), bold = i == index), maxLines = 1)
            }
        }
    }
}

// ── 상태: 레벨·성격 / 친밀도·배부름 ───────────────────

@Composable
private fun StatusLcd(state: UiState, page: Int, dot: Dp) {
    val b = state.bung
    Column(Modifier.fillMaxWidth().padding(horizontal = dot * 5), verticalArrangement = Arrangement.spacedBy(dot * 2)) {
        if (page == 0) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Lv.${b.level}", style = pixelText((dot * 6f).asSp(), bold = true))
                Spacer(Modifier.width(dot * 3))
                val cur = CharacterEngine.expForLevel(b.level)
                val next = CharacterEngine.expForLevel(b.level + 1)
                PixelBar((b.exp - cur).toFloat() / (next - cur), dot)
            }
            Text("성격", style = pixelText((dot * 3.6f).asSp()))
            PersonalityScale(b.personality, dot)
            Text(
                "${CharacterEngine.personalityType(b.personality).label} 붕붕이",
                style = pixelText((dot * 4.2f).asSp(), bold = true),
            )
        } else {
            HeartRow("친밀도", b.intimacy, dot)
            HeartRow("배부름", b.fullness, dot)
            Text(
                "총 ${"%.0f".format(b.totalKm)}km · 도감 ${b.collected.size}/${Regions.all.size}",
                style = pixelText((dot * 3.6f).asSp()),
            )
        }
        Text("${page + 1}/2", style = pixelText((dot * 3f).asSp(), color = InkLight))
    }
}

@Composable
private fun PixelBar(value: Float, dot: Dp) {
    val filled = (value.coerceIn(0f, 1f) * 10).toInt()
    Row(horizontalArrangement = Arrangement.spacedBy(dot * 0.6f)) {
        repeat(10) { i ->
            Canvas(Modifier.size(dot * 2.4f, dot * 4)) {
                if (i < filled) drawRect(Ink) else drawRect(Ink, style = Stroke(dot.toPx() * 0.5f))
            }
        }
    }
}

@Composable
private fun PersonalityScale(p: Int, dot: Dp) {
    val pos = ((p + 100) / 200f * 10).toInt().coerceIn(0, 10)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("겁", style = pixelText((dot * 3.4f).asSp()))
        Spacer(Modifier.width(dot))
        repeat(11) { i ->
            Canvas(Modifier.size(dot * 2.6f, dot * 3)) {
                val d = dot.toPx()
                if (i == pos) drawRect(Ink, Offset(0f, 0f), Size(d * 2f, d * 3f))
                else drawRect(InkLight, Offset(d * 0.5f, d * 1.2f), Size(d, d * 0.6f))
            }
        }
        Spacer(Modifier.width(dot))
        Text("다정", style = pixelText((dot * 3.4f).asSp()))
    }
}

@Composable
private fun HeartRow(label: String, value: Int, dot: Dp) {
    val hearts = ((value + 10) / 20).coerceIn(0, 5)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = pixelText((dot * 3.8f).asSp()), modifier = Modifier.width(dot * 18))
        repeat(5) { i ->
            PixelIcon(if (i < hearts) PixelArt.heartFull else PixelArt.heartEmpty, dot * 0.9f, Modifier.padding(end = dot))
        }
    }
}

// ── 지역 도감 ─────────────────────────────────

@Composable
private fun DexLcd(state: UiState, index: Int, dot: Dp, onTap: (Int) -> Unit) {
    val collected = state.bung.collected
    val cols = 7
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LcdText("도감 ${collected.size}/${Regions.all.size}", dot, bold = true, maxLines = 1)
        Spacer(Modifier.height(dot * 2))
        Regions.all.chunked(cols).forEachIndexed { rowIdx, row ->
            Row {
                row.forEachIndexed { colIdx, r ->
                    val i = rowIdx * cols + colIdx
                    val have = r.id in collected
                    val cell = dot * 13
                    Box(
                        Modifier.size(cell).clickable { onTap(i) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (i == index) {
                            Canvas(Modifier.size(cell)) {
                                val d = dot.toPx() * 0.6f
                                drawRect(Ink, Offset(d, d), Size(size.width - 2 * d, size.height - 2 * d), style = Stroke(d))
                            }
                        }
                        val sprite = if (have) PixelArt.items[r.id] ?: PixelArt.unknownItem else PixelArt.unknownItem
                        PixelIcon(sprite, dot * 1.1f, ink = if (have) Ink else InkGhost, light = if (have) InkLight else InkGhost)
                    }
                }
            }
        }
        Spacer(Modifier.height(dot * 2))
        val r = Regions.all[index]
        val have = r.id in collected
        val desc = when {
            !have -> "${r.name} · 아직 못 가 봤어"
            state.bung.equippedItem == r.id -> "${r.name} · ${r.itemName} (착용 중)"
            else -> "${r.name} · ${r.itemName}"
        }
        LcdText(desc, dot, maxLines = 1)
    }
}

// ── 주차 후: 오늘의 기록 ──────────────────────────

private fun reportPages(r: GrowthReport): List<List<String>> {
    val p = r.personalityDelta
    val lines = mutableListOf(
        "달린 거리 ${"%.1f".format(r.trip.distanceKm)}km",
        "경험치 +${r.expGained}",
        "급제동 ${r.trip.harshBrakes} · 급가속 ${r.trip.harshAccels}",
        when {
            p > 0 -> "다정해졌어 +$p"
            p < 0 -> "겁이 늘었어 +${-p}"
            else -> "성격 그대로"
        },
        "친밀도 +${r.intimacyDelta}",
    )
    if (r.leveledUp) lines += "레벨 업! Lv.${r.newLevel}"
    r.newItems.forEach { lines += "${it.name} · ${it.itemName}" }
    return lines.chunked(3)
}

@Composable
private fun ReportLcd(pages: List<List<String>>, index: Int, dot: Dp) {
    if (pages.isEmpty()) return
    val page = pages[index.coerceIn(0, pages.size - 1)]
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(dot * 2)) {
        LcdText("오늘의 기록 ${index + 1}/${pages.size}", dot, bold = true, maxLines = 1)
        page.forEach { LcdText(it, dot, scale = 1.05f, maxLines = 1) }
    }
}

// ── 주행 중: 액정은 단순하게, 붕붕이는 길 위를 달린다 ────────

@Composable
private fun DrivingLcd(state: UiState, dot: Dp) {
    val v = state.vehicle
    val frame = rememberFrame(180L)
    var scroll by remember { mutableIntStateOf(0) }
    LaunchedEffect(frame) { scroll = (scroll + 2) % 10 }
    Column(
        Modifier.fillMaxSize().padding(dot * 3),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("${v?.speedKmh?.toInt() ?: 0}", style = pixelText((dot * 12f).asSp(), bold = true))
            Spacer(Modifier.width(dot))
            Text("km/h", style = pixelText((dot * 4f).asSp()), modifier = Modifier.padding(bottom = dot * 2))
        }
        val region = v?.let { Regions.detect(it.lat, it.lon) }
        LcdText(
            "${region?.name ?: "이동 중"} · 연료 ${v?.fuelPercent ?: 0}%",
            dot,
            scale = 0.9f,
            maxLines = 1,
        )
        Canvas(Modifier.fillMaxWidth().height(dot * 26)) {
            val d = dot.toPx()
            val roadY = size.height - 6 * d
            val tiles = (size.width / (40 * d)).toInt() + 2
            for (t in 0 until tiles) drawSprite(PixelArt.road, t * 40 * d - scroll * d, roadY, d)
            val bump = if (frame == 0) 0f else d
            drawSprite(bungSprite(state.mood), size.width / 2 - 10 * d, roadY - 15 * d - bump, d)
        }
        LcdText(state.speech.ifBlank { "붕붕이가 함께 달리는 중" }, dot, maxLines = 2)
    }
}
