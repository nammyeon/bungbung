package com.bungbung.app.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.sp
import com.bungbung.core.Mood
import com.bungbung.core.PersonalityType

/** 성격에 따라 몸 색이 달라진다. 다정할수록 따뜻한 노랑, 겁이 많을수록 창백한 파랑. */
fun bodyColor(type: PersonalityType): Color = when (type) {
    PersonalityType.GENTLE -> Color(0xFFFFB84C)
    PersonalityType.MILD -> Color(0xFFFFC93C)
    PersonalityType.NORMAL -> Color(0xFFFFD970)
    PersonalityType.SENSITIVE -> Color(0xFFB8D8E8)
    PersonalityType.TIMID -> Color(0xFF9DB4D6)
}

private val Ink = Color(0xFF2B2D42)

/**
 * 붕붕이 캐릭터. 작은 자동차 모양에 표정이 있다. 기분에 따라 표정과 움직임이 바뀐다.
 * equippedEmoji: 도감에서 얻은 아이템(머리 위에 얹는다).
 */
@Composable
fun BungCharacter(
    mood: Mood,
    personality: PersonalityType,
    equippedEmoji: String?,
    modifier: Modifier = Modifier,
) {
    val t = rememberInfiniteTransition(label = "bung")
    val speed = when (mood) {
        Mood.HAPPY -> 500
        Mood.SCARED -> 140
        Mood.SICK, Mood.SULKY, Mood.HUNGRY, Mood.SLEEPY -> 1400
        Mood.CALM -> 900
    }
    val bounce by t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(speed, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bounce",
    )
    val body = bodyColor(personality)

    Box(modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val amp = when (mood) {
                Mood.HAPPY -> 0.05f
                Mood.SCARED -> 0.012f
                else -> 0.02f
            }
            val dy = -bounce * w * amp
            val dx = if (mood == Mood.SCARED) (bounce - 0.5f) * w * 0.02f else 0f
            // 그림자
            drawOval(Color(0x33000000), Offset(w * 0.2f, w * 0.86f), Size(w * 0.6f, w * 0.06f))
            translate(dx, dy) { drawBung(body, mood) }
        }
        if (equippedEmoji != null) {
            Text(
                equippedEmoji,
                fontSize = 44.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .layout { measurable, constraints ->
                        val p = measurable.measure(constraints)
                        layout(p.width, p.height) { p.place(0, (constraints.maxHeight * 0.06f - bounce * 8f).toInt()) }
                    },
            )
        }
    }
}

private fun DrawScope.drawBung(body: Color, mood: Mood) {
    val w = size.width
    // 바퀴
    drawCircle(Ink, w * 0.09f, Offset(w * 0.3f, w * 0.8f))
    drawCircle(Ink, w * 0.09f, Offset(w * 0.7f, w * 0.8f))
    drawCircle(Color(0xFF8D99AE), w * 0.035f, Offset(w * 0.3f, w * 0.8f))
    drawCircle(Color(0xFF8D99AE), w * 0.035f, Offset(w * 0.7f, w * 0.8f))
    // 몸통
    drawRoundRect(body, Offset(w * 0.14f, w * 0.3f), Size(w * 0.72f, w * 0.5f), CornerRadius(w * 0.18f))
    // 앞유리(이마)
    drawRoundRect(Color(0x55FFFFFF), Offset(w * 0.26f, w * 0.34f), Size(w * 0.48f, w * 0.1f), CornerRadius(w * 0.05f))
    // 볼
    val cheek = if (mood == Mood.SULKY) Color(0x88FF6B6B) else Color(0x55FF8FA3)
    val cheekR = if (mood == Mood.SULKY) w * 0.06f else w * 0.045f
    drawCircle(cheek, cheekR, Offset(w * 0.27f, w * 0.62f))
    drawCircle(cheek, cheekR, Offset(w * 0.73f, w * 0.62f))
    // 헤드라이트
    drawCircle(Color(0xFFFFF3B0), w * 0.03f, Offset(w * 0.2f, w * 0.7f))
    drawCircle(Color(0xFFFFF3B0), w * 0.03f, Offset(w * 0.8f, w * 0.7f))

    val le = Offset(w * 0.39f, w * 0.54f)
    val re = Offset(w * 0.61f, w * 0.54f)
    val stroke = Stroke(width = w * 0.022f, cap = StrokeCap.Round)
    when (mood) {
        Mood.HAPPY -> {
            listOf(le, re).forEach { c ->
                drawArc(Ink, 200f, 140f, false, Offset(c.x - w * 0.045f, c.y - w * 0.02f), Size(w * 0.09f, w * 0.08f), style = stroke)
            }
            drawArc(Ink, 20f, 140f, false, Offset(w * 0.43f, w * 0.58f), Size(w * 0.14f, w * 0.1f), style = stroke)
        }
        Mood.CALM -> {
            drawCircle(Ink, w * 0.028f, le)
            drawCircle(Ink, w * 0.028f, re)
            drawArc(Ink, 30f, 120f, false, Offset(w * 0.45f, w * 0.6f), Size(w * 0.1f, w * 0.06f), style = stroke)
        }
        Mood.SCARED -> {
            listOf(le, re).forEach { c ->
                drawCircle(Color.White, w * 0.045f, c)
                drawCircle(Ink, w * 0.018f, c)
            }
            drawOval(Ink, Offset(w * 0.47f, w * 0.63f), Size(w * 0.06f, w * 0.07f))
            // 식은땀
            val drop = Path().apply {
                moveTo(w * 0.8f, w * 0.36f)
                quadraticTo(w * 0.84f, w * 0.44f, w * 0.8f, w * 0.46f)
                quadraticTo(w * 0.76f, w * 0.44f, w * 0.8f, w * 0.36f)
            }
            drawPath(drop, Color(0xFF7FD1F7))
        }
        Mood.SULKY -> {
            drawLine(Ink, Offset(le.x - w * 0.04f, le.y), Offset(le.x + w * 0.04f, le.y), w * 0.022f, StrokeCap.Round)
            drawLine(Ink, Offset(re.x - w * 0.04f, re.y), Offset(re.x + w * 0.04f, re.y), w * 0.022f, StrokeCap.Round)
            drawLine(Ink, Offset(w * 0.45f, w * 0.65f), Offset(w * 0.55f, w * 0.65f), w * 0.022f, StrokeCap.Round)
        }
        Mood.HUNGRY -> {
            listOf(le, re).forEach { c ->
                drawArc(Ink, 20f, 140f, false, Offset(c.x - w * 0.04f, c.y - w * 0.03f), Size(w * 0.08f, w * 0.06f), style = stroke)
            }
            drawOval(Ink, Offset(w * 0.45f, w * 0.6f), Size(w * 0.1f, w * 0.08f))
        }
        Mood.SICK -> {
            listOf(le, re).forEach { c ->
                val d = w * 0.03f
                drawLine(Ink, Offset(c.x - d, c.y - d), Offset(c.x + d, c.y + d), w * 0.018f, StrokeCap.Round)
                drawLine(Ink, Offset(c.x - d, c.y + d), Offset(c.x + d, c.y - d), w * 0.018f, StrokeCap.Round)
            }
            drawArc(Ink, 200f, 140f, false, Offset(w * 0.44f, w * 0.63f), Size(w * 0.12f, w * 0.06f), style = stroke)
            // 반창고
            drawRoundRect(Color(0xFFF4D6B0), Offset(w * 0.6f, w * 0.31f), Size(w * 0.16f, w * 0.06f), CornerRadius(w * 0.02f))
        }
        Mood.SLEEPY -> {
            drawLine(Ink, Offset(le.x - w * 0.04f, le.y), Offset(le.x + w * 0.04f, le.y), w * 0.018f, StrokeCap.Round)
            drawLine(Ink, Offset(re.x - w * 0.04f, re.y), Offset(re.x + w * 0.04f, re.y), w * 0.018f, StrokeCap.Round)
        }
    }
}
