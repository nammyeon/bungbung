package com.bungbung.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.bungbung.app.R
import com.bungbung.core.Mood
import kotlinx.coroutines.delay

/** LCD 색: 옛날 다마고치 액정처럼 연두빛 회색 바탕 + 진한 잉크 */
val LcdBg = Color(0xFFB9C7A0)
val Ink = Color(0xFF2E3A2B)
val InkLight = Color(0xFF7E8F6C)
val InkGhost = Color(0x332E3A2B)

val Galmuri = FontFamily(
    Font(R.font.galmuri11, FontWeight.Normal),
    Font(R.font.galmuri11_bold, FontWeight.Bold),
)

fun pixelText(size: TextUnit, bold: Boolean = false, color: Color = Ink) =
    TextStyle(fontFamily = Galmuri, fontSize = size, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, color = color)

@Composable
fun Dp.asSp(): TextUnit = with(LocalDensity.current) { this@asSp.toSp() }

/** 스프라이트를 점 단위로 그린다. 점 사이에 아주 작은 틈을 둬서 액정 느낌을 낸다. */
fun DrawScope.drawSprite(s: Sprite, x: Float, y: Float, dot: Float, ink: Color = Ink, light: Color = InkLight) {
    val gap = dot * 0.12f
    s.rows.forEachIndexed { r, row ->
        row.forEachIndexed { c, ch ->
            val color = when (ch) {
                '#' -> ink
                'o' -> light
                else -> null
            }
            if (color != null) drawRect(color, Offset(x + c * dot, y + r * dot), Size(dot - gap, dot - gap))
        }
    }
}

@Composable
fun PixelIcon(s: Sprite, dot: Dp, modifier: Modifier = Modifier, ink: Color = Ink, light: Color = InkLight) {
    Canvas(modifier.size(dot * s.width, dot * s.height)) {
        drawSprite(s, 0f, 0f, dot.toPx(), ink, light)
    }
}

fun faceFor(mood: Mood): Sprite = when (mood) {
    Mood.HAPPY -> PixelArt.faceHappy
    Mood.CALM -> PixelArt.faceCalm
    Mood.SCARED -> PixelArt.faceScared
    Mood.SULKY -> PixelArt.faceSulky
    Mood.HUNGRY -> PixelArt.faceHungry
    Mood.SICK -> PixelArt.faceSick
    Mood.SLEEPY -> PixelArt.faceSleepy
}

/** 몸 + 표정을 합친 한 장. */
fun bungSprite(mood: Mood): Sprite {
    val rows = PixelArt.body.rows.map { StringBuilder(it) }
    faceFor(mood).rows.forEachIndexed { dy, row ->
        row.forEachIndexed { dx, ch -> if (ch != '.') rows[PixelArt.FACE_TOP + dy].setCharAt(dx, ch) }
    }
    return Sprite(rows.map { it.toString() })
}

/** 1초에 몇 번 바뀌는 애니메이션 프레임(0, 1). */
@Composable
fun rememberFrame(periodMs: Long): Int {
    var frame by remember { mutableIntStateOf(0) }
    LaunchedEffect(periodMs) {
        while (true) {
            delay(periodMs)
            frame = 1 - frame
        }
    }
    return frame
}

/**
 * 도트 붕붕이. 기분에 따라 표정·움직임·효과(Zzz, 땀, 느낌표)가 바뀐다.
 * 머리 위에는 도감에서 얻은 아이템을 얹는다.
 */
@Composable
fun PixelBung(mood: Mood, itemId: String?, dot: Dp, modifier: Modifier = Modifier) {
    val period = when (mood) {
        Mood.HAPPY -> 350L
        Mood.SCARED -> 150L
        Mood.SLEEPY, Mood.SICK, Mood.SULKY, Mood.HUNGRY -> 900L
        Mood.CALM -> 600L
    }
    val frame = rememberFrame(period)
    val sprite = bungSprite(mood)
    val item = itemId?.let { PixelArt.items[it] }
    val effect = when (mood) {
        Mood.SLEEPY -> PixelArt.zzz
        Mood.SCARED -> PixelArt.sweat
        Mood.SICK -> PixelArt.exclaim
        else -> null
    }
    // 가로: 몸 20 + 효과 자리 9 / 세로: 아이템 8 + 몸 15 + 뛰는 자리 2
    Canvas(modifier.size(dot * 29, dot * 25)) {
        val d = dot.toPx()
        val jump = when (mood) {
            Mood.HAPPY -> frame * 2
            Mood.CALM -> frame
            else -> 0
        }
        val shake = if (mood == Mood.SCARED) (frame * 2 - 1) * 0.5f else 0f
        val x = (4 + shake) * d
        val y = (10 - jump) * d
        drawSprite(sprite, x, y, d)
        if (item != null) drawSprite(item, x + 5.5f * d, y - 7 * d, d)
        if (effect != null) {
            val ey = if (frame == 0) 1f else 0f
            drawSprite(effect, x + 20 * d, (2 + ey) * d, d)
        }
    }
}
