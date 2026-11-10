package com.bungbung.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bungbung.app.BungViewModel
import com.bungbung.app.Phase
import com.bungbung.app.UiState
import com.bungbung.core.CharacterEngine
import com.bungbung.core.Condition
import com.bungbung.core.DriveScenario
import com.bungbung.core.GrowthReport
import com.bungbung.core.Mood
import com.bungbung.core.Regions

// 밝은 파스텔 톤: 크림색 배경 + 흰 카드 + 노랑 포인트
private val Bg = Color(0xFFFFF8EC)
private val BgBottom = Color(0xFFFFE9D6)
private val Panel = Color(0xFFFFFFFF)
private val PanelHi = Color(0xFFFFF1C7)
private val Line = Color(0xFFF1E3CC)
private val TextMain = Color(0xFF3A3445)
private val TextSub = Color(0xFF8C8499)
private val Accent = Color(0xFFFFB703)
private val AccentText = Color(0xFFE08A00)
private val Warn = Color(0xFFE5636B)
private val ExpColor = Color(0xFFFFC93C)
private val LoveColor = Color(0xFFFF8FAB)
private val FoodColor = Color(0xFF7BD389)
private val MindColor = Color(0xFFA78BFA)

@Composable
fun BungApp(state: UiState, vm: BungViewModel) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Bg, BgBottom)))
    ) {
        when (state.phase) {
            Phase.OFF -> OffScreen(state, vm)
            Phase.GREETING -> GreetingScreen(state, vm)
            Phase.DRIVING -> DrivingScreen(state, vm)
            Phase.PARKED -> ParkedScreen(state, vm)
        }
        if (state.showDex) DexScreen(state, vm)
    }
}

/** 가로(차량 화면)면 좌우로, 세로(휴대폰)면 위아래로 배치한다. */
@Composable
private fun TwoPane(left: @Composable (Modifier) -> Unit, right: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize().padding(24.dp)) {
        if (maxWidth > maxHeight) {
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { left(Modifier.fillMaxWidth(0.8f)) }
                Spacer(Modifier.width(24.dp))
                Column(Modifier.weight(1.1f).verticalScroll(rememberScrollState()), content = right)
            }
        } else {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                left(Modifier.fillMaxWidth(0.7f))
                Spacer(Modifier.height(16.dp))
                Column(Modifier.fillMaxWidth(), content = right)
            }
        }
    }
}

@Composable
private fun Character(state: UiState, mood: Mood = state.mood, modifier: Modifier = Modifier) {
    val type = CharacterEngine.personalityType(state.bung.personality)
    val emoji = state.bung.equippedItem?.let { Regions.byId(it)?.itemEmoji }
    BungCharacter(mood, type, emoji, modifier)
}

@Composable
private fun SpeechBubble(text: String) {
    AnimatedVisibility(visible = text.isNotBlank(), enter = fadeIn(), exit = fadeOut()) {
        Text(
            text,
            color = TextMain,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(24.dp, 24.dp, 24.dp, 6.dp))
                .background(Panel, RoundedCornerShape(24.dp, 24.dp, 24.dp, 6.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun StatusPanel(state: UiState) {
    val b = state.bung
    val type = CharacterEngine.personalityType(b.personality)
    val nextLevelExp = CharacterEngine.expForLevel(b.level + 1)
    val curLevelExp = CharacterEngine.expForLevel(b.level)
    Column(
        Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp))
            .background(Panel, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("붕붕", color = TextMain, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(10.dp))
            Text(
                "Lv.${b.level}",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.background(Accent, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 2.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text("${type.label} 성격", color = AccentText, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
        StatBar("⭐ 경험치", (b.exp - curLevelExp).toFloat() / (nextLevelExp - curLevelExp), "${b.exp}/${nextLevelExp}", ExpColor)
        StatBar("💗 친밀도", b.intimacy / 100f, "${b.intimacy}", LoveColor)
        StatBar("🍙 배부름", b.fullness / 100f, "${b.fullness}", FoodColor)
        StatBar("🌱 성격", (b.personality + 100) / 200f, "겁 많음 ↔ 다정함", MindColor)
        conditionLabel(state.condition)?.let { Text(it, color = Warn, fontSize = 16.sp) }
    }
}

private fun conditionLabel(c: Condition): String? = when (c) {
    Condition.SICK -> "⚠ 차량 경고 → 붕붕이가 아파요"
    Condition.SORE_FOOT -> "⚠ 타이어 공기압 낮음 → 발이 아파요"
    Condition.HUNGRY -> "⚠ 연료 부족 → 배고파요"
    Condition.NORMAL -> null
}

@Composable
private fun StatBar(label: String, value: Float, trailing: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextSub, fontSize = 15.sp, modifier = Modifier.width(88.dp))
        LinearProgressIndicator(
            progress = { value.coerceIn(0f, 1f) },
            modifier = Modifier.weight(1f).height(12.dp).clip(RoundedCornerShape(50)),
            color = color,
            trackColor = Line,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        Spacer(Modifier.width(10.dp))
        Text(trailing, color = TextSub, fontSize = 13.sp)
    }
}

@Composable
private fun BigButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, primary: Boolean = true) {
    if (primary) {
        Button(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White),
            shape = RoundedCornerShape(50),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
        ) { Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            shape = RoundedCornerShape(50),
            border = BorderStroke(2.dp, Line),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Panel),
        ) {
            Text(text, fontSize = 17.sp, color = TextMain)
        }
    }
}

// ── 시동 꺼짐: 시연 시나리오 고르기 ─────────────────────

@Composable
private fun OffScreen(state: UiState, vm: BungViewModel) {
    TwoPane(
        left = { m ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Character(state, Mood.SLEEPY, m)
                Text("시동 꺼짐 · 붕붕이가 자고 있어요", color = TextSub, fontSize = 16.sp)
            }
        },
        right = {
            StatusPanel(state)
            Spacer(Modifier.height(16.dp))
            Text("시연 시나리오", color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            DriveScenario.all.forEach { sc ->
                val selected = sc.id == state.scenario.id
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(if (selected) PanelHi else Panel, RoundedCornerShape(16.dp))
                        .border(2.dp, if (selected) Accent else Line, RoundedCornerShape(16.dp))
                        .clickable { vm.selectScenario(sc.id) }
                        .padding(12.dp),
                ) {
                    Text(sc.title, color = TextMain, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text(sc.description, color = TextSub, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            BigButton("🔑 시동 걸기", vm::ignition, Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BigButton("도감", { vm.openDex(true) }, Modifier.weight(1f), primary = false)
                BigButton("3일 뒤로", { vm.skipDays(3) }, Modifier.weight(1f), primary = false)
                BigButton("초기화", vm::resetAll, Modifier.weight(1f), primary = false)
            }
            if (state.dayOffset > 0) {
                Text("시연용 날짜: 오늘 +${state.dayOffset}일", color = TextSub, fontSize = 13.sp)
            }
        },
    )
}

// ── 시동 직후: 붕붕이가 먼저 말을 건다 ────────────────────

@Composable
private fun GreetingScreen(state: UiState, vm: BungViewModel) {
    TwoPane(
        left = { m -> Character(state, modifier = m.clickable { vm.pet() }) },
        right = {
            SpeechBubble(state.speech)
            Spacer(Modifier.height(16.dp))
            StatusPanel(state)
            Spacer(Modifier.height(16.dp))
            BigButton("출발 (D)", vm::depart, Modifier.fillMaxWidth())
        },
    )
}

// ── 주행 중: 화면은 내비에게 양보, 붕붕이는 목소리와 상단 알림으로만 ────────

@Composable
private fun DrivingScreen(state: UiState, vm: BungViewModel) {
    val v = state.vehicle
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("주행 중", color = AccentText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(12.dp))
            Text(state.scenario.title, color = TextSub, fontSize = 16.sp)
            Spacer(Modifier.weight(1f))
            // 에뮬레이터 시연을 빨리 끝내기 위한 버튼(실차에서는 기어 P로 자동 처리)
            Text(
                "시연: 바로 주차",
                color = TextSub,
                fontSize = 13.sp,
                modifier = Modifier
                    .border(1.dp, TextSub, RoundedCornerShape(8.dp))
                    .clickable { vm.park() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(3.dp, RoundedCornerShape(24.dp))
                .background(Panel, RoundedCornerShape(24.dp))
                .padding(24.dp),
        ) {
            Column(Modifier.align(Alignment.CenterStart)) {
                Text("${v?.speedKmh?.toInt() ?: 0}", color = TextMain, fontSize = 96.sp, fontWeight = FontWeight.Bold)
                Text("km/h", color = TextSub, fontSize = 20.sp)
                Spacer(Modifier.height(12.dp))
                val region = v?.let { Regions.detect(it.lat, it.lon) }
                Text(
                    "현재 위치: ${region?.name ?: "이동 중"} · 주행 ${"%.1f".format(((v?.odometerKm ?: 0.0) - 12_345.0).coerceAtLeast(0.0))} km · 연료 ${v?.fuelPercent ?: 0}%",
                    color = TextSub,
                    fontSize = 16.sp,
                )
            }
            Column(Modifier.align(Alignment.TopEnd).width(280.dp)) {
                Text("붕붕이가 기록 중", color = TextSub, fontSize = 14.sp)
                state.driveLog.forEach { Text("· $it", color = TextMain, fontSize = 14.sp) }
            }
            Text(
                "이 자리는 내비게이션 화면이에요. 붕붕이는 화면을 가리지 않고 목소리와 상단 알림으로만 등장해요. (주행 중 터치 조작 잠김)",
                color = TextSub,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.BottomStart),
            )
        }
        if (state.speech.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Character(state, modifier = Modifier.size(64.dp))
                Spacer(Modifier.width(8.dp))
                Text("🔊 ${state.speech}", color = TextMain, fontSize = 18.sp)
            }
        }
    }
}

// ── 주차: 오늘의 성장 카드 + 돌보기 ─────────────────────────

@Composable
private fun ParkedScreen(state: UiState, vm: BungViewModel) {
    TwoPane(
        left = { m ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Character(state, modifier = m.clickable { vm.pet() })
                Text("붕붕이를 눌러서 쓰다듬어 주세요", color = TextSub, fontSize = 14.sp)
            }
        },
        right = {
            SpeechBubble(state.speech)
            Spacer(Modifier.height(12.dp))
            state.report?.let { GrowthCard(it) }
            Spacer(Modifier.height(12.dp))
            StatusPanel(state)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BigButton("🍪 간식", vm::snack, Modifier.weight(1f))
                BigButton("🤚 쓰다듬기", vm::pet, Modifier.weight(1f))
                BigButton("📖 도감", { vm.openDex(true) }, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if ((state.vehicle?.fuelPercent ?: 100) < 50) {
                    BigButton("⛽ 주유하기", vm::refuel, Modifier.weight(1f))
                }
                BigButton("시동 끄기", vm::turnOff, Modifier.weight(1f), primary = false)
            }
        },
    )
}

@Composable
private fun GrowthCard(r: GrowthReport) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(PanelHi, RoundedCornerShape(20.dp))
            .border(2.dp, Accent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text("✨ 오늘의 성장 카드", color = AccentText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("주행 거리 ${"%.1f".format(r.trip.distanceKm)} km  →  경험치 +${r.expGained}", color = TextMain, fontSize = 16.sp)
        Text("급제동 ${r.trip.harshBrakes}회 · 급가속 ${r.trip.harshAccels}회", color = TextMain, fontSize = 16.sp)
        val p = r.personalityDelta
        Text(
            when {
                p > 0 -> "성격: 다정함 +$p (부드러운 운전 덕분이에요)"
                p < 0 -> "성격: 겁 많음 ${-p} 증가 (급한 운전에 놀랐어요)"
                else -> "성격 변화 없음"
            },
            color = if (p < 0) Warn else TextMain,
            fontSize = 16.sp,
        )
        Text("친밀도 +${r.intimacyDelta}", color = TextMain, fontSize = 16.sp)
        if (r.leveledUp) Text("🎉 레벨 업! Lv.${r.newLevel}", color = AccentText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        if (r.newItems.isNotEmpty()) {
            Text("새 도감: " + r.newItems.joinToString(" ") { "${it.itemEmoji}${it.name}" }, color = TextMain, fontSize = 16.sp)
        }
    }
}

// ── 지역 도감 · 꾸미기 ─────────────────────────────

@Composable
private fun DexScreen(state: UiState, vm: BungViewModel) {
    val collected = state.bung.collected
    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Bg, BgBottom)))
            .padding(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("지역 도감", color = TextMain, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(12.dp))
            Text("${collected.size} / ${Regions.all.size}", color = AccentText, fontSize = 20.sp)
            Spacer(Modifier.weight(1f))
            BigButton("닫기", { vm.openDex(false) }, primary = false)
        }
        Text("처음 가 본 지역마다 아이템을 얻어요. 얻은 아이템을 누르면 붕붕이가 착용해요.", color = TextSub, fontSize = 15.sp)
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(Regions.all, key = { it.id }) { r ->
                val have = r.id in collected
                val equipped = state.bung.equippedItem == r.id
                Column(
                    Modifier
                        .background(if (have) Panel else Line.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .border(3.dp, if (equipped) Accent else Color.Transparent, RoundedCornerShape(18.dp))
                        .clickable(enabled = have) { vm.equip(if (equipped) null else r.id) }
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(if (have) r.itemEmoji else "❔", fontSize = 40.sp)
                    Text(r.name, color = if (have) TextMain else TextSub, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        if (have) (if (equipped) "착용 중" else r.itemName) else "아직 안 가 봤어요",
                        color = TextSub,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
