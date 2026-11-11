package com.bungbung.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.bungbung.app.R
import com.bungbung.core.Mood
import com.bungbung.core.PersonalityType

/** 그림의 한 겹: SVG 경로 + 채우기 색 + 외곽선 색·두께. 색이 OUT이면 외곽선 잉크, BODY면 붕붕이 몸 색. */
class L(val d: String, val fill: Long?, val stroke: Long?, val width: Float)

/** 그림 한 장. (x0, y0)부터 w x h 크기의 좌표계로 그려져 있다. */
class Drawing(val x0: Float, val y0: Float, val w: Float, val h: Float, val layers: List<L>)

val Ink = Color(0xFF3D3030)
val Jua = FontFamily(Font(R.font.jua))

fun juaText(size: TextUnit, color: Color = Ink) = TextStyle(fontFamily = Jua, fontSize = size, color = color)

/** 성격에 따라 몸 색이 달라진다. 다정할수록 따뜻한 노랑, 겁이 많을수록 하늘색. */
fun bodyColor(type: PersonalityType): Color = when (type) {
    PersonalityType.GENTLE -> Color(0xFFFFD98A)
    PersonalityType.MILD -> Color(0xFFFFE9A8)
    PersonalityType.NORMAL -> Color(0xFFFFF1CC)
    PersonalityType.SENSITIVE -> Color(0xFFD9EEFF)
    PersonalityType.TIMID -> Color(0xFFCFD8F5)
}

private val pathCache = HashMap<String, Path>()

private fun pathOf(d: String): Path = pathCache.getOrPut(d) { PathParser().parsePathString(d).toPath() }

private fun colorOf(v: Long, body: Color): Color = when (v) {
    OUT -> Ink
    BODY -> body
    else -> Color(v)
}

/** (left, top)에 그림의 (0, 0)이 오도록 scale배로 그린다. */
fun DrawScope.drawArt(a: Drawing, left: Float, top: Float, scale: Float, body: Color = Color(0xFFFFE9A8)) {
    withTransform({
        translate(left, top)
        scale(scale, scale, pivot = Offset.Zero)
    }) {
        for (l in a.layers) {
            val p = pathOf(l.d)
            l.fill?.let { drawPath(p, colorOf(it, body)) }
            val stroke = l.stroke
            if (stroke != null && l.width > 0.05f) {
                drawPath(p, colorOf(stroke, body), style = Stroke(width = l.width, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}

/** 배경 그림을 화면에 꽉 차게(넘치는 부분은 잘라서) 맞추는 계산. */
class Fit(val scale: Float, val left: Float, val top: Float) {
    fun x(sceneX: Float) = left + sceneX * scale
    fun y(sceneY: Float) = top + sceneY * scale
}

fun cover(a: Drawing, w: Float, h: Float): Fit {
    val s = maxOf(w / a.w, h / a.h)
    return Fit(s, (w - a.w * s) / 2 - a.x0 * s, (h - a.h * s) / 2 - a.y0 * s)
}

fun faceFor(mood: Mood): Drawing = when (mood) {
    Mood.HAPPY -> Art.faceHappy
    Mood.CALM -> Art.faceCalm
    Mood.SCARED -> Art.faceScared
    Mood.SULKY -> Art.faceSulky
    Mood.HUNGRY -> Art.faceHungry
    Mood.SICK -> Art.faceSick
    Mood.SLEEPY -> Art.faceSleepy
}

/** 붕붕이가 차지하는 화면 영역(말풍선 위치, 터치 판정에 쓴다). */
class BungBox(val left: Float, val top: Float, val scale: Float) {
    val right get() = left + 120 * scale
    val headTop get() = top + 14 * scale
    val bottom get() = top + 120 * scale
    fun contains(o: Offset) = o.x in left..right && o.y in headTop..bottom
}

/** 발끝이 (cx, feetY)에 오도록, 키가 height인 붕붕이의 위치. */
fun bungBox(cx: Float, feetY: Float, height: Float): BungBox {
    val a = Art.bungBack
    val s = height / a.h
    return BungBox(cx - 60 * s, feetY - (a.h + a.y0) * s, s)
}

/** 붕붕이 그리기. squash: 통통 튀는 효과(+면 납작, -면 길쭉). */
fun DrawScope.drawBung(box: BungBox, mood: Mood, itemId: String?, body: Color, squash: Float = 0f, shakeX: Float = 0f) {
    val pivot = Offset(box.left + 60 * box.scale, box.bottom)
    withTransform({
        translate(shakeX, 0f)
        scale(1f + squash, 1f - squash, pivot)
    }) {
        drawArt(Art.bungBack, box.left, box.top, box.scale, body)
        drawArt(faceFor(mood), box.left, box.top, box.scale, body)
        itemId?.let { Art.items[it] }?.let { drawArt(it, box.left + 42 * box.scale, box.top - 10 * box.scale, box.scale * 0.9f, body) }
    }
}

/** 48 x 48 아이콘이나 40 x 40 아이템 그림을 size 크기로. */
@Composable
fun ArtIcon(a: Drawing, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        drawArt(a, -a.x0 * this.size.width / a.w, -a.y0 * this.size.width / a.w, this.size.width / a.w)
    }
}
