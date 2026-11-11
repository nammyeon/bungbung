package com.bungbung.app.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bungbung.app.BungViewModel
import com.bungbung.app.Phase
import com.bungbung.app.UiState
import com.bungbung.core.CharacterEngine
import com.bungbung.core.DriveScenario
import com.bungbung.core.GrowthReport
import com.bungbung.core.Mood
import com.bungbung.core.Regions
import kotlin.math.PI
import kotlin.math.sin

private val Cream = Color(0xFFFFFBF4)
private val Brown = Color(0xFF5B4636)
private val SubText = Color(0xFF8C7B70)
private val Track = Color(0xFFEFE8E0)
private val Red = Color(0xFFFF6B6B)
private val Green = Color(0xFF5CCB8F)
private val Purple = Color(0xFFB794F6)
private val BubblePurple = Color(0xFFB57BFF)
private val NavBg = Color(0xFFE3CDB0)
private val NavShade = Color(0xFFCDB08C)

private enum class Sheet { NONE, SCENARIO, DEX, STATUS, DEMO, REPORT }

@Composable
fun BungApp(state: UiState, vm: BungViewModel) {
    var sheet by remember { mutableStateOf(Sheet.NONE) }
    LaunchedEffect(state.phase) {
        sheet = if (state.phase == Phase.PARKED && state.report != null) Sheet.REPORT else Sheet.NONE
    }

    Box(Modifier.fillMaxSize().background(Cream)) {
        if (state.phase == Phase.DRIVING) {
            DrivingScene(state, vm)
        } else {
            RoomScene(state, onPet = vm::pet)
            Hud(state, Modifier.align(Alignment.TopStart).padding(16.dp))
            SideButtons(
                Modifier.align(Alignment.CenterStart).padding(start = 20.dp),
                state,
                onStatus = { sheet = Sheet.STATUS },
                onDemo = { sheet = Sheet.DEMO },
            )
            BottomNav(
                state,
                Modifier.align(Alignment.BottomCenter),
                onSnack = vm::snack,
                onPet = vm::pet,
                onKey = {
                    when (state.phase) {
                        Phase.OFF -> sheet = Sheet.SCENARIO
                        Phase.GREETING -> vm.depart()
                        Phase.PARKED -> vm.turnOff()
                        Phase.DRIVING -> Unit
                    }
                },
                onDex = { sheet = Sheet.DEX },
                onFuel = vm::refuel,
            )
        }

        val close = { sheet = Sheet.NONE }
        when (sheet) {
            Sheet.NONE -> Unit
            Sheet.SCENARIO -> SheetFrame("어디로 갈까?", close) {
                DriveScenario.all.forEach { sc ->
                    val selected = sc.id == state.scenario.id
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (selected) Color(0xFFFFE1EA) else Color(0xFFFFF1F4))
                            .clickable {
                                vm.selectScenario(sc.id)
                                vm.ignition()
                                sheet = Sheet.NONE
                            }
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                    ) {
                        Text(sc.title, style = juaText(20.sp))
                        Text(sc.description, style = juaText(15.sp, SubText))
                    }
                }
            }
            Sheet.DEX -> SheetFrame("지역 도감  ${state.bung.collected.size}/${Regions.all.size}", close) { DexGrid(state, vm) }
            Sheet.STATUS -> SheetFrame("붕붕이 상태", close) { StatusBody(state) }
            Sheet.DEMO -> SheetFrame("시연 도구", close) {
                Text("발표·시연 영상을 찍을 때 쓰는 버튼이에요.", style = juaText(16.sp, SubText))
                Spacer(Modifier.height(12.dp))
                PillButton("3일 뒤로 가기 (삐짐 보기)", enabled = state.phase == Phase.OFF) { vm.skipDays(3); sheet = Sheet.NONE }
                Spacer(Modifier.height(8.dp))
                PillButton("처음부터 다시", color = Color(0xFFFF9FB2), enabled = state.phase == Phase.OFF) { vm.resetAll(); sheet = Sheet.NONE }
                if (state.phase != Phase.OFF) {
                    Spacer(Modifier.height(8.dp))
                    Text("시동을 끈 상태에서만 쓸 수 있어요.", style = juaText(14.sp, SubText))
                }
                if (state.dayOffset > 0) {
                    Spacer(Modifier.height(8.dp))
                    Text("지금 시연 날짜: 오늘 +${state.dayOffset}일", style = juaText(14.sp, SubText))
                }
            }
            Sheet.REPORT -> state.report?.let { r -> SheetFrame("오늘의 기록", close) { ReportBody(r, close) } }
        }
    }
}

// ── 방 장면 ──────────────────────────────────

@Composable
private fun bounce(mood: Mood): Pair<Float, Float> {
    val t = rememberInfiniteTransition(label = "bung")
    val period = when (mood) {
        Mood.HAPPY -> 700
        Mood.SCARED -> 260
        Mood.SLEEPY, Mood.SICK, Mood.SULKY, Mood.HUNGRY -> 2200
        Mood.CALM -> 1400
    }
    val phase by t.animateFloat(0f, 1f, infiniteRepeatable(tween(period, easing = LinearEasing), RepeatMode.Restart), label = "p")
    val wave = sin(phase * 2 * PI).toFloat()
    val squash = when (mood) {
        Mood.HAPPY -> wave * 0.05f
        Mood.SCARED -> 0f
        else -> wave * 0.025f
    }
    val shake = if (mood == Mood.SCARED) wave * 3f else 0f
    return squash to shake
}

@Composable
private fun RoomScene(state: UiState, onPet: () -> Unit) {
    val mood = if (state.phase == Phase.OFF && state.speech.isBlank()) Mood.SLEEPY else state.mood
    val body = bodyColor(CharacterEngine.personalityType(state.bung.personality))
    val (squash, shake) = bounce(mood)
    val pet by rememberUpdatedState(onPet)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val fit = cover(Art.room, wPx, hPx)
        val height = minOf(hPx * 0.5f, wPx * 0.34f)
        val box = bungBox(fit.x(200f), fit.y(196f), height)
        val boxNow by rememberUpdatedState(box)

        Canvas(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures { if (boxNow.contains(it)) pet() } },
        ) {
            drawArt(Art.room, fit.left, fit.top, fit.scale)
            drawBung(box, mood, state.bung.equippedItem, body, squash, shake)
        }
        val bubbleBottom = if (state.bung.equippedItem != null) box.top - 12 * box.scale else box.headTop
        SpeechBubble(state.speech.ifBlank { if (state.phase == Phase.OFF) "쿨쿨…" else "" }, box.left + 60 * box.scale, bubbleBottom)
    }
}

/** (cx, bottom) 위에 꼬리가 오도록 말풍선을 띄운다. */
@Composable
private fun SpeechBubble(text: String, cx: Float, bottom: Float) {
    if (text.isBlank()) return
    Column(
        Modifier
            .widthIn(max = 420.dp)
            .layout { m, c ->
                val p = m.measure(c.copy(minWidth = 0, minHeight = 0))
                layout(p.width, p.height) {
                    p.place((cx - p.width / 2f).toInt().coerceAtLeast(8), (bottom - p.height).toInt().coerceAtLeast(8))
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text,
            style = juaText(22.sp, Color.White),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(BubblePurple)
                .padding(horizontal = 20.dp, vertical = 10.dp),
        )
        Box(
            Modifier
                .size(18.dp, 12.dp)
                .background(BubblePurple, GenericShape { size, _ ->
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2, size.height)
                    close()
                }),
        )
    }
}

// ── 위쪽 정보창 ───────────────────────────────

@Composable
private fun Hud(state: UiState, modifier: Modifier) {
    val b = state.bung
    val type = CharacterEngine.personalityType(b.personality)
    val cur = CharacterEngine.expForLevel(b.level)
    val next = CharacterEngine.expForLevel(b.level + 1)
    val dday = b.bornDay?.let { state.today - it + 1 } ?: 1
    Column(modifier.width(420.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White.copy(alpha = 0.94f))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(state, 60.dp)
            Spacer(Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Chip("D+$dday", Color(0xFFFF6B7D), Color(0xFFFFE8EC))
                    Spacer(Modifier.width(6.dp))
                    Text("붕붕", style = juaText(24.sp))
                    Spacer(Modifier.width(6.dp))
                    Chip("${type.label} 성격", Color(0xFF8A6BD1), Color(0xFFF1E9FF))
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center) {
                        ArtIcon(Art.iconHeart, 36.dp)
                        Text(
                            "${b.level}",
                            style = juaText(16.sp, Color.White).copy(shadow = Shadow(Ink, Offset(0f, 1.5f), 2f)),
                            modifier = Modifier.padding(bottom = 3.dp),
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Gauge((b.exp - cur).toFloat() / (next - cur), "${b.exp}/$next", Red, Modifier.width(230.dp))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = 0.94f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ArtIcon(Art.iconFood, 26.dp)
            Spacer(Modifier.width(4.dp))
            Gauge(b.fullness / 100f, "${b.fullness}/100", Green, Modifier.weight(1f))
            Spacer(Modifier.width(14.dp))
            ArtIcon(Art.iconLove, 24.dp)
            Spacer(Modifier.width(4.dp))
            Gauge(b.intimacy / 100f, "${b.intimacy}/100", Purple, Modifier.weight(1f))
        }
    }
}

@Composable
private fun Avatar(state: UiState, size: Dp) {
    val body = bodyColor(CharacterEngine.personalityType(state.bung.personality))
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White)
            .border(3.dp, Color(0xFFF3E6D6), CircleShape),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val s = this.size.width / 100f
            drawArt(Art.bungBack, -10 * s, -22 * s, s, body)
            drawArt(Art.faceCalm, -10 * s, -22 * s, s, body)
        }
    }
}

@Composable
private fun Chip(text: String, fg: Color, bg: Color) {
    Text(
        text,
        style = juaText(14.sp, fg),
        modifier = Modifier.clip(RoundedCornerShape(9.dp)).background(bg).padding(horizontal = 7.dp, vertical = 1.dp),
    )
}

@Composable
private fun Gauge(value: Float, label: String, color: Color, modifier: Modifier) {
    Box(
        modifier
            .height(18.dp)
            .clip(RoundedCornerShape(50))
            .background(Track),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .fillMaxWidth(value.coerceIn(0.04f, 1f))
                .clip(RoundedCornerShape(50))
                .background(color),
        )
        Text(label, style = juaText(13.sp, Color.White).copy(shadow = Shadow(Color(0x55000000), Offset(0f, 1f), 2f)))
    }
}

// ── 왼쪽 동그란 버튼 ──────────────────────────

@Composable
private fun SideButtons(modifier: Modifier, state: UiState, onStatus: () -> Unit, onDemo: () -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        RoundButton("상태", Color(0xFFFFC2D1), onStatus) { Avatar(state, 50.dp) }
        RoundButton("시연", Color(0xFF8FD9B6), onDemo) { ArtIcon(Art.iconDemo, 36.dp) }
    }
}

@Composable
private fun RoundButton(label: String, ring: Color, onClick: () -> Unit, content: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(4.dp, ring, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) { content() }
        Text(label, style = juaText(14.sp, Brown), modifier = Modifier.padding(top = 2.dp))
    }
}

// ── 아래 메뉴 ────────────────────────────────

@Composable
private fun BottomNav(
    state: UiState,
    modifier: Modifier,
    onSnack: () -> Unit,
    onPet: () -> Unit,
    onKey: () -> Unit,
    onDex: () -> Unit,
    onFuel: () -> Unit,
) {
    val (keyIcon, keyLabel) = when (state.phase) {
        Phase.GREETING -> Art.iconGo to "출발"
        Phase.PARKED -> Art.iconKey to "시동 끄기"
        else -> Art.iconKey to "시동 걸기"
    }
    Row(
        modifier
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(NavBg)
            .drawBehind { drawRect(NavShade, Offset(0f, size.height - 6.dp.toPx())) }
            .padding(start = 28.dp, end = 28.dp, top = 10.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        NavButton(Art.iconSnack, "간식", 62.dp, onSnack)
        NavButton(Art.iconPet, "쓰다듬기", 62.dp, onPet)
        NavButton(keyIcon, keyLabel, 76.dp, onKey, highlight = true)
        NavButton(Art.iconDex, "도감", 62.dp, onDex)
        NavButton(Art.iconFuel, "주유", 62.dp, onFuel)
    }
}

@Composable
private fun NavButton(icon: Drawing, label: String, size: Dp, onClick: () -> Unit, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(size)
                .drawBehind { drawCircle(NavShade, this.size.minDimension / 2, center + Offset(0f, 4.dp.toPx())) }
                .clip(CircleShape)
                .background(if (highlight) Color(0xFFFFF0B8) else Color(0xFFFFF8EE))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) { ArtIcon(icon, size * 0.72f) }
        Text(label, style = juaText(15.sp, Brown), modifier = Modifier.padding(top = 6.dp))
    }
}

// ── 주행 장면 ────────────────────────────────

@Composable
private fun DrivingScene(state: UiState, vm: BungViewModel) {
    val v = state.vehicle
    val speed = (v?.speedKmh ?: 0.0).toFloat()
    val t = rememberInfiniteTransition(label = "road")
    val loop by t.animateFloat(0f, 1f, infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "loop")
    val body = bodyColor(CharacterEngine.personalityType(state.bung.personality))
    val (squash, shake) = bounce(state.mood)
    // 속도가 빠를수록 풍경이 빨리 지나간다
    var travelled by remember { mutableStateOf(0f) }
    LaunchedEffect(loop) { travelled = (travelled + speed * 0.06f) % 800f }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val fit = cover(Art.road, wPx, hPx)
        val box = bungBox(fit.x(200f), fit.y(208f), minOf(hPx * 0.36f, wPx * 0.25f))
        Canvas(Modifier.fillMaxSize()) {
            drawArt(Art.road, fit.left, fit.top, fit.scale)
            val clouds = (travelled * 0.2f) % 800f
            val trees = travelled % 800f
            val dash = (travelled * 2f) % 800f
            for (k in 0..1) {
                drawArt(Art.roadClouds, fit.x(-clouds + k * 800f), fit.top, fit.scale)
                drawArt(Art.roadTrees, fit.x(-trees + k * 800f), fit.top, fit.scale)
                drawArt(Art.roadDash, fit.x(-dash + k * 800f), fit.top, fit.scale)
            }
            drawBung(box, state.mood, state.bung.equippedItem, body, squash, shake)
        }
        SpeechBubble(state.speech, box.left + 60 * box.scale, if (state.bung.equippedItem != null) box.top - 12 * box.scale else box.headTop)

        Column(Modifier.align(Alignment.TopStart).padding(20.dp)) {
            Row(
                Modifier.clip(RoundedCornerShape(26.dp)).background(Color.White).padding(horizontal = 22.dp, vertical = 6.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text("${speed.toInt()}", style = juaText(44.sp))
                Spacer(Modifier.width(6.dp))
                Text("km/h", style = juaText(20.sp, SubText), modifier = Modifier.padding(bottom = 8.dp))
            }
            Spacer(Modifier.height(8.dp))
            val region = v?.let { Regions.detect(it.lat, it.lon) }
            Text(
                "${region?.let { "${it.name} 지나는 중" } ?: "달리는 중"} · 연료 ${v?.fuelPercent ?: 0}%",
                style = juaText(18.sp),
                modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.9f)).padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }
        Text(
            "바로 주차 (시연용)",
            style = juaText(15.sp, SubText),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .clickable { vm.park() }
                .padding(horizontal = 14.dp, vertical = 8.dp),
        )
        Text(
            "주행 중에는 화면 조작이 잠겨요",
            style = juaText(16.sp, Color.White),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Ink.copy(alpha = 0.72f))
                .padding(horizontal = 18.dp, vertical = 8.dp),
        )
    }
}

// ── 팝업 창 ─────────────────────────────────

@Composable
private fun SheetFrame(title: String, onClose: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClose),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(28.dp))
                .background(Cream)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                .padding(24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = juaText(26.sp), modifier = Modifier.weight(1f))
                ArtIcon(Art.iconClose, 40.dp, Modifier.clip(CircleShape).clickable(onClick = onClose))
            }
            Spacer(Modifier.height(12.dp))
            Column(Modifier.verticalScroll(rememberScrollState())) { content() }
        }
    }
}

@Composable
private fun PillButton(text: String, color: Color = Color(0xFFB794F6), enabled: Boolean = true, onClick: () -> Unit) {
    Text(
        text,
        style = juaText(19.sp, Color.White),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.4f)
            .clip(RoundedCornerShape(50))
            .background(color)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp),
    )
}

@Composable
private fun DexGrid(state: UiState, vm: BungViewModel) {
    Text("처음 가 본 지역마다 선물을 받아요. 누르면 붕붕이가 착용해요.", style = juaText(15.sp, SubText))
    Spacer(Modifier.height(12.dp))
    Regions.all.chunked(5).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            row.forEach { r ->
                val have = r.id in state.bung.collected
                val equipped = state.bung.equippedItem == r.id
                Column(
                    Modifier
                        .width(92.dp)
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = have) { vm.equip(if (equipped) null else r.id) }
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier
                            .size(66.dp)
                            .clip(CircleShape)
                            .background(if (have) Color.White else Color(0xFFF3ECE3))
                            .border(3.dp, if (equipped) Color(0xFFFF9FB2) else Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) { ArtIcon(if (have) Art.items[r.id] ?: Art.itemUnknown else Art.itemUnknown, 50.dp) }
                    Text(r.name, style = juaText(16.sp, if (have) Ink else SubText), modifier = Modifier.padding(top = 4.dp))
                    Text(
                        if (equipped) "착용 중" else if (have) r.itemName else "???",
                        style = juaText(12.sp, if (equipped) Color(0xFFFF6B7D) else SubText),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBody(state: UiState) {
    val b = state.bung
    val type = CharacterEngine.personalityType(b.personality)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Avatar(state, 84.dp)
        Spacer(Modifier.width(16.dp))
        Column {
            Text("Lv.${b.level} · ${type.label} 성격", style = juaText(22.sp))
            Text("함께 달린 거리 ${"%.0f".format(b.totalKm)}km", style = juaText(16.sp, SubText))
            Text("모은 도감 ${b.collected.size}/${Regions.all.size}", style = juaText(16.sp, SubText))
        }
    }
    Spacer(Modifier.height(16.dp))
    Text("성격", style = juaText(17.sp, Brown))
    Spacer(Modifier.height(6.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("겁 많음", style = juaText(14.sp, SubText))
        Spacer(Modifier.width(8.dp))
        Box(Modifier.weight(1f).height(16.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                val h = size.height
                drawRoundRect(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(Color(0xFFCFD8F5), Color(0xFFFFF1CC), Color(0xFFFFD98A))),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(h / 2),
                )
                val x = (b.personality + 100) / 200f * size.width
                drawCircle(Color.White, h * 0.75f, Offset(x.coerceIn(h, size.width - h), h / 2))
                drawCircle(Ink, h * 0.75f, Offset(x.coerceIn(h, size.width - h), h / 2), style = androidx.compose.ui.graphics.drawscope.Stroke(3.dp.toPx()))
            }
        }
        Spacer(Modifier.width(8.dp))
        Text("다정함", style = juaText(14.sp, SubText))
    }
    Spacer(Modifier.height(12.dp))
    Text("부드럽게 운전하면 다정해지고, 급정거·급출발이 많으면 겁이 많아져요.", style = juaText(15.sp, SubText))
}

@Composable
private fun ReportBody(r: GrowthReport, onClose: () -> Unit) {
    val p = r.personalityDelta
    ReportLine(Art.iconGo, "달린 거리", "${"%.1f".format(r.trip.distanceKm)}km")
    ReportLine(Art.iconHeart, "경험치", "+${r.expGained}")
    ReportLine(Art.iconKey, "급제동 · 급출발", "${r.trip.harshBrakes}번 · ${r.trip.harshAccels}번")
    ReportLine(
        Art.iconLove,
        "성격",
        when {
            p > 0 -> "다정함 +$p"
            p < 0 -> "겁 +${-p}"
            else -> "그대로"
        },
    )
    ReportLine(Art.iconPet, "친밀도", "+${r.intimacyDelta}")
    if (r.leveledUp) {
        Text(
            "레벨 업! 이제 Lv.${r.newLevel}",
            style = juaText(22.sp, Color(0xFFFF6B7D)),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        )
    }
    if (r.newItems.isNotEmpty()) {
        Text("새로 받은 선물", style = juaText(17.sp, Brown), modifier = Modifier.padding(top = 6.dp))
        Row(Modifier.padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            r.newItems.forEach { item ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(54.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                        ArtIcon(Art.items[item.id] ?: Art.itemUnknown, 42.dp)
                    }
                    Text(item.name, style = juaText(13.sp, SubText))
                }
            }
        }
    }
    Spacer(Modifier.height(12.dp))
    PillButton("좋아!", onClick = onClose)
}

@Composable
private fun ReportLine(icon: Drawing, label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        ArtIcon(icon, 30.dp)
        Spacer(Modifier.width(10.dp))
        Text(label, style = juaText(18.sp, Brown), modifier = Modifier.weight(1f))
        Text(value, style = juaText(20.sp))
    }
}
