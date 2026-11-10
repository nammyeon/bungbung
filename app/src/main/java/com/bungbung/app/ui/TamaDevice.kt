package com.bungbung.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val PageBg = Color(0xFFFFEEF2)
private val PageDot = Color(0xFFFFD6E0)
private val Shell = Color(0xFFFF9EB8)
private val ShellDark = Color(0xFFE77C99)
private val ShellLight = Color(0xFFFFC9D7)
private val Bezel = Color(0xFFFFF6EA)
private val BtnYellow = Color(0xFFFFD84D)
private val BtnShadow = Color(0xFFD9A922)
private val Brown = Color(0xFF8A4B5E)

data class ButtonLabels(val a: String, val b: String, val c: String)

/**
 * 다마고치 기계: 분홍 알 모양 본체 + 가운데 액정 + 아래 버튼 3개.
 * 차량 화면(가로)에서도 휴대폰(세로)에서도 화면 안에 꽉 차게 맞춘다.
 */
@Composable
fun TamaDevice(
    labels: ButtonLabels,
    onA: () -> Unit,
    onB: () -> Unit,
    onC: () -> Unit,
    lcd: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(PageBg)
            .drawBehind {
                val step = 28.dp.toPx()
                var y = 0f
                var row = 0
                while (y < size.height) {
                    var x = if (row % 2 == 0) 0f else step / 2
                    while (x < size.width) {
                        drawCircle(PageDot, 3.dp.toPx(), Offset(x, y))
                        x += step
                    }
                    y += step / 2
                    row++
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        val eggH: Dp = minOf(maxHeight * 0.96f, maxWidth / 0.8f)
        val eggW: Dp = eggH * 0.8f
        Box(Modifier.size(eggW, eggH)) {
            Canvas(Modifier.matchParentSize()) { drawEgg() }

            Text(
                "BUNGBUNG",
                style = pixelText((eggH * 0.035f).asSp(), bold = true, color = Brown),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = eggH * 0.1f),
            )

            // 액정
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = eggH * 0.16f)
                    .size(eggW * 0.74f, eggH * 0.5f)
                    .clip(RoundedCornerShape(eggW * 0.07f))
                    .background(Bezel)
                    .padding(eggW * 0.035f)
                    .clip(RoundedCornerShape(eggW * 0.025f))
                    .background(LcdBg),
                content = lcd,
            )

            val d = eggW * 0.15f
            TamaButton(labels.a, d, onA, Modifier.offset(eggW * 0.27f - d / 2, eggH * 0.72f))
            TamaButton(labels.b, d, onB, Modifier.offset(eggW * 0.5f - d / 2, eggH * 0.755f))
            TamaButton(labels.c, d, onC, Modifier.offset(eggW * 0.73f - d / 2, eggH * 0.72f))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawEgg() {
    val w = size.width
    val h = size.height
    val egg = Path().apply {
        moveTo(w / 2, 0f)
        cubicTo(w * 0.93f, 0f, w, h * 0.42f, w, h * 0.62f)
        cubicTo(w, h * 0.88f, w * 0.78f, h, w / 2, h)
        cubicTo(w * 0.22f, h, 0f, h * 0.88f, 0f, h * 0.62f)
        cubicTo(0f, h * 0.42f, w * 0.07f, 0f, w / 2, 0f)
        close()
    }
    translate(0f, h * 0.012f) { drawPath(egg, ShellDark) }
    drawPath(egg, Shell)
    drawPath(egg, ShellDark, style = Stroke(width = w * 0.012f))
    // 반짝이
    drawOval(ShellLight, Offset(w * 0.25f, h * 0.085f), Size(w * 0.12f, h * 0.04f))
    drawOval(ShellLight, Offset(w * 0.2f, h * 0.14f), Size(w * 0.04f, h * 0.02f))
}

@Composable
private fun TamaButton(label: String, d: Dp, onClick: () -> Unit, modifier: Modifier) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    Column(modifier.width(d), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(d)
                .drawBehind {
                    drawCircle(BtnShadow, size.minDimension / 2, center + Offset(0f, size.height * 0.08f))
                }
                .offset(y = if (pressed) d * 0.06f else 0.dp)
                .clip(CircleShape)
                .background(BtnYellow)
                .clickable(interactionSource = source, indication = null, onClick = onClick),
        ) {
            Canvas(Modifier.matchParentSize()) {
                drawCircle(Color(0x66FFFFFF), size.minDimension * 0.16f, Offset(size.width * 0.34f, size.height * 0.32f))
            }
        }
        Text(
            label,
            style = pixelText((d * 0.2f).asSp(), color = Brown),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = d * 0.12f),
        )
    }
}
