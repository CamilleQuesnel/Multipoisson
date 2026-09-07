package com.multipoisson.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*

// ─────────────────────────────────────────────────────────────────────────────
// Public entry point
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun FishMascot(level: Int, size: Dp = 120.dp, animate: Boolean = false) {
    val isSpecial = level >= 6

    // Idle bob animation
    val infiniteTransition = rememberInfiniteTransition(label = "mascot")
    val bob by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bob",
    )

    // Spectral entrance for special creatures
    val entranceScale = remember { Animatable(if (isSpecial && animate) 0f else 1f) }
    val entranceAlpha = remember { Animatable(if (isSpecial && animate) 0f else 1f) }
    LaunchedEffect(level) {
        if (isSpecial && animate) {
            entranceAlpha.animateTo(1f, tween(300))
            entranceScale.animateTo(1.3f, spring(dampingRatio = 0.4f, stiffness = 220f))
            entranceScale.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 300f))
        }
    }

    val bobOffset = if (animate) (bob - 0.5f) * 6f else 0f

    Canvas(
        modifier = Modifier.size(size),
        onDraw = {
            val s = size.toPx()
            withTransform({
                translate(0f, bobOffset)
                scale(entranceScale.value, pivot = Offset(s / 2f, s / 2f))
            }) {
                drawMascot(level, s, alpha = entranceAlpha.value)
            }
        },
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Dispatcher
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawMascot(level: Int, s: Float, alpha: Float) {
    when (level) {
        1    -> drawFish1(s, alpha)
        2    -> drawFish2(s, alpha)
        3    -> drawFish3(s, alpha)
        4    -> drawFish4(s, alpha)
        5    -> drawFish5(s, alpha)
        6    -> drawDragon(s, alpha)
        7    -> drawNarval(s, alpha)
        8    -> drawBeluga(s, alpha)
        9    -> drawRequinMarteau(s, alpha)
        10   -> drawOrque(s, alpha)
        11   -> drawBaleineBosse(s, alpha)
        12   -> drawPieuvre(s, alpha)
        13   -> drawEspadon(s, alpha)
        14   -> drawTortue(s, alpha)
        15   -> drawGRB(s, alpha)
        16   -> drawMegalodont(s, alpha)
        17   -> drawCapibara(s, alpha)
        18   -> drawLeviathan(s, alpha)
        19   -> drawPoseidon(s, alpha)
        else -> drawFish1(s, alpha)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helper extensions
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.ovalAt(cx: Float, cy: Float, rx: Float, ry: Float, color: Color, alpha: Float = 1f) {
    drawOval(
        color = color.copy(alpha = color.alpha * alpha),
        topLeft = Offset(cx - rx, cy - ry),
        size = Size(rx * 2, ry * 2),
    )
}

private fun DrawScope.circleAt(cx: Float, cy: Float, r: Float, color: Color, alpha: Float = 1f) {
    drawCircle(color = color.copy(alpha = color.alpha * alpha), radius = r, center = Offset(cx, cy))
}

private fun DrawScope.tailPath(cx: Float, cy: Float, w: Float, h: Float, color: Color, alpha: Float = 1f): Path {
    return Path().apply {
        moveTo(cx, cy - h / 2)
        lineTo(cx + w, cy)
        lineTo(cx, cy + h / 2)
        close()
    }.also { drawPath(it, color.copy(alpha = color.alpha * alpha)) }
}

private fun Color.withA(a: Float) = copy(alpha = a)

// ─────────────────────────────────────────────────────────────────────────────
// Levels 1–5 : simple fish
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawFish1(s: Float, alpha: Float) {
    val cx = s * 0.48f; val cy = s * 0.52f
    // tail
    val tail = Path().apply {
        moveTo(s * 0.82f, cy)
        lineTo(s * 1.0f, cy - s * 0.18f)
        lineTo(s * 1.0f, cy + s * 0.18f)
        close()
    }
    drawPath(tail, Color(0xFF88CCFF).withA(alpha))
    // body
    ovalAt(cx, cy, s * 0.40f, s * 0.24f, Color(0xFF44AAFF), alpha)
    // eye
    circleAt(cx - s * 0.12f, cy - s * 0.06f, s * 0.055f, Color.White, alpha)
    circleAt(cx - s * 0.12f, cy - s * 0.06f, s * 0.03f, Color.Black, alpha)
    // fin
    val fin = Path().apply {
        moveTo(cx + s * 0.05f, cy - s * 0.22f)
        lineTo(cx + s * 0.20f, cy - s * 0.38f)
        lineTo(cx + s * 0.28f, cy - s * 0.14f)
        close()
    }
    drawPath(fin, Color(0xFF88CCFF).withA(alpha))
}

private fun DrawScope.drawFish2(s: Float, alpha: Float) {
    val cx = s * 0.48f; val cy = s * 0.52f
    val tail = Path().apply {
        moveTo(s * 0.82f, cy)
        lineTo(s * 1.0f, cy - s * 0.20f)
        lineTo(s * 1.0f, cy + s * 0.20f)
        close()
    }
    drawPath(tail, Color(0xFFFFAA44).withA(alpha))
    ovalAt(cx, cy, s * 0.40f, s * 0.24f, Color(0xFFFF7700), alpha)
    // stripes
    for (i in 0..2) {
        val x = cx - s * 0.15f + i * s * 0.12f
        drawLine(Color.White.withA(alpha * 0.5f), Offset(x, cy - s * 0.20f), Offset(x, cy + s * 0.20f), strokeWidth = s * 0.025f)
    }
    circleAt(cx - s * 0.14f, cy - s * 0.07f, s * 0.055f, Color.White, alpha)
    circleAt(cx - s * 0.14f, cy - s * 0.07f, s * 0.03f, Color.Black, alpha)
}

private fun DrawScope.drawFish3(s: Float, alpha: Float) {
    val cx = s * 0.48f; val cy = s * 0.52f
    val tail = Path().apply {
        moveTo(s * 0.82f, cy)
        lineTo(s * 1.0f, cy - s * 0.20f)
        lineTo(s * 1.0f, cy + s * 0.20f)
        close()
    }
    drawPath(tail, Color(0xFF88FF88).withA(alpha))
    ovalAt(cx, cy, s * 0.40f, s * 0.24f, Color(0xFF22CC44), alpha)
    // shimmer dots
    val dots = listOf(Offset(cx - 0.1f * s, cy), Offset(cx + 0.1f * s, cy - 0.05f * s), Offset(cx + 0.05f * s, cy + 0.1f * s))
    dots.forEach { circleAt(it.x, it.y, s * 0.03f, Color.White.withA(0.7f * alpha)) }
    circleAt(cx - s * 0.14f, cy - s * 0.07f, s * 0.055f, Color.White, alpha)
    circleAt(cx - s * 0.14f, cy - s * 0.07f, s * 0.03f, Color.Black, alpha)
}

private fun DrawScope.drawFish4(s: Float, alpha: Float) {
    val cx = s * 0.48f; val cy = s * 0.52f
    val tail = Path().apply {
        moveTo(s * 0.82f, cy)
        lineTo(s * 1.0f, cy - s * 0.22f)
        lineTo(s * 1.0f, cy + s * 0.22f)
        close()
    }
    drawPath(tail, Color(0xFFFF88CC).withA(alpha))
    ovalAt(cx, cy, s * 0.40f, s * 0.24f, Color(0xFFFF2299), alpha)
    // spots
    listOf(Offset(cx - 0.08f * s, cy + 0.06f * s), Offset(cx + 0.12f * s, cy - 0.04f * s)).forEach {
        circleAt(it.x, it.y, s * 0.065f, Color(0xFFFF88CC).withA(alpha))
    }
    circleAt(cx - s * 0.14f, cy - s * 0.07f, s * 0.055f, Color.White, alpha)
    circleAt(cx - s * 0.14f, cy - s * 0.07f, s * 0.03f, Color.Black, alpha)
}

private fun DrawScope.drawFish5(s: Float, alpha: Float) {
    val cx = s * 0.48f; val cy = s * 0.52f
    // fancy forked tail
    val tail = Path().apply {
        moveTo(s * 0.82f, cy)
        lineTo(s * 1.0f, cy - s * 0.25f)
        lineTo(s * 0.93f, cy)
        lineTo(s * 1.0f, cy + s * 0.25f)
        close()
    }
    drawPath(tail, Color(0xFFFFDD55).withA(alpha))
    ovalAt(cx, cy, s * 0.42f, s * 0.25f, Color(0xFFFF9900), alpha)
    // golden shimmer
    for (i in 0..4) {
        val angle = i * PI.toFloat() / 5f - PI.toFloat() / 2f
        val dx = cos(angle) * s * 0.28f; val dy = sin(angle) * s * 0.16f
        circleAt(cx + dx, cy + dy, s * 0.025f, Color(0xFFFFFF44).withA(0.8f * alpha))
    }
    circleAt(cx - s * 0.16f, cy - s * 0.08f, s * 0.06f, Color.White, alpha)
    circleAt(cx - s * 0.16f, cy - s * 0.08f, s * 0.035f, Color.Black, alpha)
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 6 : Dragon Bleu
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawDragon(s: Float, alpha: Float) {
    val cx = s * 0.50f; val cy = s * 0.55f
    // wings
    val wingL = Path().apply {
        moveTo(cx - s * 0.05f, cy - s * 0.10f)
        lineTo(cx - s * 0.50f, cy - s * 0.42f)
        lineTo(cx - s * 0.28f, cy + s * 0.05f)
        close()
    }
    val wingR = Path().apply {
        moveTo(cx + s * 0.05f, cy - s * 0.10f)
        lineTo(cx + s * 0.50f, cy - s * 0.42f)
        lineTo(cx + s * 0.28f, cy + s * 0.05f)
        close()
    }
    drawPath(wingL, Color(0xFF1166DD).withA(alpha))
    drawPath(wingR, Color(0xFF1166DD).withA(alpha))
    // body
    ovalAt(cx, cy, s * 0.28f, s * 0.38f, Color(0xFF1188FF), alpha)
    // tail
    val tail = Path().apply {
        moveTo(cx + s * 0.18f, cy + s * 0.28f)
        quadraticTo(cx + s * 0.50f, cy + s * 0.48f, cx + s * 0.35f, cy + s * 0.58f)
        quadraticTo(cx + s * 0.20f, cy + s * 0.52f, cx + s * 0.10f, cy + s * 0.35f)
    }
    drawPath(tail, Color(0xFF0055CC).withA(alpha), style = Stroke(width = s * 0.06f))
    // head
    ovalAt(cx, cy - s * 0.30f, s * 0.20f, s * 0.16f, Color(0xFF1188FF), alpha)
    // spines
    for (i in -1..1) {
        val sx = cx + i * s * 0.10f
        drawLine(Color(0xFF44DDFF).withA(alpha), Offset(sx, cy - s * 0.42f), Offset(sx, cy - s * 0.58f), strokeWidth = s * 0.03f)
    }
    // eyes glow
    circleAt(cx - s * 0.07f, cy - s * 0.32f, s * 0.06f, Color.White, alpha)
    circleAt(cx + s * 0.07f, cy - s * 0.32f, s * 0.06f, Color.White, alpha)
    circleAt(cx - s * 0.07f, cy - s * 0.32f, s * 0.035f, Color(0xFF00FFFF), alpha)
    circleAt(cx + s * 0.07f, cy - s * 0.32f, s * 0.035f, Color(0xFF00FFFF), alpha)
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 7 : Narval
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawNarval(s: Float, alpha: Float) {
    val cx = s * 0.50f; val cy = s * 0.58f
    // body
    ovalAt(cx, cy, s * 0.32f, s * 0.22f, Color(0xFFAABBFF), alpha)
    // forehead bump
    ovalAt(cx - s * 0.12f, cy - s * 0.20f, s * 0.18f, s * 0.14f, Color(0xFFAABBFF), alpha)
    // tusk (spiral by segments)
    val tuskStart = Offset(cx - s * 0.26f, cy - s * 0.24f)
    for (i in 0..8) {
        val t = i / 8f
        val tx = tuskStart.x - t * s * 0.38f
        val ty = tuskStart.y - t * s * 0.08f + sin(t * PI.toFloat() * 3f) * s * 0.025f
        if (i > 0) {
            val pt = (i - 1) / 8f
            val px = tuskStart.x - pt * s * 0.38f
            val py = tuskStart.y - pt * s * 0.08f + sin(pt * PI.toFloat() * 3f) * s * 0.025f
            drawLine(Color(0xFFFFFFDD).withA(alpha), Offset(px, py), Offset(tx, ty), strokeWidth = s * 0.025f * (1f - t * 0.7f))
        }
    }
    // flippers
    val flipL = Path().apply {
        moveTo(cx - s * 0.10f, cy + s * 0.08f)
        lineTo(cx - s * 0.32f, cy + s * 0.30f)
        lineTo(cx, cy + s * 0.14f)
        close()
    }
    drawPath(flipL, Color(0xFF8899EE).withA(alpha))
    // tail fin
    val tail = Path().apply {
        moveTo(cx + s * 0.28f, cy)
        lineTo(cx + s * 0.48f, cy - s * 0.16f)
        lineTo(cx + s * 0.50f, cy + s * 0.16f)
        close()
    }
    drawPath(tail, Color(0xFF8899EE).withA(alpha))
    // eye
    circleAt(cx - s * 0.12f, cy - s * 0.06f, s * 0.055f, Color.White, alpha)
    circleAt(cx - s * 0.12f, cy - s * 0.06f, s * 0.03f, Color.Black, alpha)
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 8 : Béluga
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawBeluga(s: Float, alpha: Float) {
    val cx = s * 0.48f; val cy = s * 0.54f
    ovalAt(cx, cy, s * 0.40f, s * 0.28f, Color(0xFFF0F8FF), alpha)
    // big round melon (forehead)
    ovalAt(cx - s * 0.22f, cy - s * 0.22f, s * 0.18f, s * 0.16f, Color(0xFFFFFFFF), alpha)
    // rosy cheeks
    circleAt(cx - s * 0.05f, cy + s * 0.04f, s * 0.09f, Color(0xFFFFCCCC).withA(0.6f * alpha))
    // cute hearts
    for (dx in listOf(-0.10f, 0.12f)) {
        val hx = cx + dx * s; val hy = cy - s * 0.08f
        val heart = Path().apply {
            moveTo(hx, hy + s * 0.04f)
            cubicTo(hx - s * 0.05f, hy, hx - s * 0.05f, hy - s * 0.06f, hx, hy - s * 0.04f)
            cubicTo(hx + s * 0.05f, hy - s * 0.06f, hx + s * 0.05f, hy, hx, hy + s * 0.04f)
            close()
        }
        drawPath(heart, Color(0xFFFFAABB).withA(alpha))
    }
    // tail
    val tail = Path().apply {
        moveTo(cx + s * 0.36f, cy)
        lineTo(cx + s * 0.52f, cy - s * 0.18f)
        lineTo(cx + s * 0.52f, cy + s * 0.18f)
        close()
    }
    drawPath(tail, Color(0xFFDDEEFF).withA(alpha))
    // smile
    val smile = Path().apply {
        moveTo(cx - s * 0.14f, cy + s * 0.08f)
        quadraticTo(cx - s * 0.05f, cy + s * 0.18f, cx + s * 0.04f, cy + s * 0.08f)
    }
    drawPath(smile, Color(0xFFCCCCDD).withA(alpha), style = Stroke(width = s * 0.025f))
    // eye
    circleAt(cx - s * 0.20f, cy - s * 0.08f, s * 0.05f, Color(0xFF334466), alpha)
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 9 : Requin Marteau
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawRequinMarteau(s: Float, alpha: Float) {
    val cx = s * 0.50f; val cy = s * 0.55f
    // body
    ovalAt(cx, cy, s * 0.36f, s * 0.22f, Color(0xFF778899), alpha)
    // white belly
    ovalAt(cx, cy + s * 0.06f, s * 0.28f, s * 0.13f, Color(0xFFEEEEEE), alpha)
    // T-shaped head (hammer)
    ovalAt(cx - s * 0.32f, cy - s * 0.04f, s * 0.10f, s * 0.22f, Color(0xFF667788), alpha)
    // dorsal fin
    val dorsal = Path().apply {
        moveTo(cx - s * 0.06f, cy - s * 0.22f)
        lineTo(cx + s * 0.04f, cy - s * 0.46f)
        lineTo(cx + s * 0.14f, cy - s * 0.22f)
        close()
    }
    drawPath(dorsal, Color(0xFF556677).withA(alpha))
    // pectoral fins
    val pectL = Path().apply {
        moveTo(cx - s * 0.08f, cy + s * 0.02f)
        lineTo(cx - s * 0.32f, cy + s * 0.24f)
        lineTo(cx + s * 0.04f, cy + s * 0.08f)
        close()
    }
    drawPath(pectL, Color(0xFF667788).withA(alpha))
    // tail
    val tail = Path().apply {
        moveTo(cx + s * 0.34f, cy)
        lineTo(cx + s * 0.52f, cy - s * 0.22f)
        lineTo(cx + s * 0.46f, cy)
        lineTo(cx + s * 0.52f, cy + s * 0.14f)
        close()
    }
    drawPath(tail, Color(0xFF667788).withA(alpha))
    // eyes on hammer tips
    circleAt(cx - s * 0.40f, cy - s * 0.10f, s * 0.04f, Color.White, alpha)
    circleAt(cx - s * 0.40f, cy - s * 0.10f, s * 0.022f, Color.Black, alpha)
    circleAt(cx - s * 0.40f, cy + s * 0.10f, s * 0.04f, Color.White, alpha)
    circleAt(cx - s * 0.40f, cy + s * 0.10f, s * 0.022f, Color.Black, alpha)
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 10 : Orque
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawOrque(s: Float, alpha: Float) {
    val cx = s * 0.48f; val cy = s * 0.54f
    // black body
    ovalAt(cx, cy, s * 0.40f, s * 0.28f, Color(0xFF111111), alpha)
    // white belly patch
    ovalAt(cx + s * 0.06f, cy + s * 0.06f, s * 0.22f, s * 0.16f, Color.White.withA(alpha))
    // eye patch
    ovalAt(cx - s * 0.16f, cy - s * 0.08f, s * 0.10f, s * 0.07f, Color.White.withA(alpha))
    // dorsal fin (tall)
    val dorsal = Path().apply {
        moveTo(cx + s * 0.02f, cy - s * 0.26f)
        lineTo(cx + s * 0.08f, cy - s * 0.58f)
        lineTo(cx + s * 0.20f, cy - s * 0.26f)
        close()
    }
    drawPath(dorsal, Color(0xFF111111).withA(alpha))
    // tail
    val tail = Path().apply {
        moveTo(cx + s * 0.36f, cy + s * 0.04f)
        lineTo(cx + s * 0.54f, cy - s * 0.18f)
        lineTo(cx + s * 0.46f, cy + s * 0.04f)
        lineTo(cx + s * 0.54f, cy + s * 0.22f)
        close()
    }
    drawPath(tail, Color(0xFF111111).withA(alpha))
    // pectorals
    val pect = Path().apply {
        moveTo(cx - s * 0.06f, cy + s * 0.06f)
        lineTo(cx - s * 0.30f, cy + s * 0.32f)
        lineTo(cx + s * 0.10f, cy + s * 0.14f)
        close()
    }
    drawPath(pect, Color(0xFF222222).withA(alpha))
    // eye (in white patch)
    circleAt(cx - s * 0.16f, cy - s * 0.08f, s * 0.03f, Color.Black, alpha)
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 11 : Baleine à Bosse
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawBaleineBosse(s: Float, alpha: Float) {
    val cx = s * 0.46f; val cy = s * 0.56f
    ovalAt(cx, cy, s * 0.42f, s * 0.26f, Color(0xFF334488), alpha)
    // lighter belly
    ovalAt(cx + s * 0.04f, cy + s * 0.08f, s * 0.30f, s * 0.14f, Color(0xFFAABBCC), alpha)
    // very long pectoral fins
    val finL = Path().apply {
        moveTo(cx - s * 0.02f, cy + s * 0.10f)
        lineTo(cx - s * 0.44f, cy + s * 0.52f)
        lineTo(cx + s * 0.12f, cy + s * 0.18f)
        close()
    }
    val finR = Path().apply {
        moveTo(cx + s * 0.12f, cy + s * 0.06f)
        lineTo(cx + s * 0.24f, cy + s * 0.42f)
        lineTo(cx + s * 0.30f, cy + s * 0.10f)
        close()
    }
    drawPath(finL, Color(0xFF2233AA).withA(alpha))
    drawPath(finR, Color(0xFF2233AA).withA(alpha))
    // hump
    val hump = Path().apply {
        moveTo(cx - s * 0.08f, cy - s * 0.24f)
        quadraticTo(cx + s * 0.06f, cy - s * 0.40f, cx + s * 0.20f, cy - s * 0.24f)
    }
    drawPath(hump, Color(0xFF334488).withA(alpha), style = Stroke(width = s * 0.06f))
    // tail flukes
    val tail = Path().apply {
        moveTo(cx + s * 0.40f, cy + s * 0.04f)
        lineTo(cx + s * 0.56f, cy - s * 0.18f)
        lineTo(cx + s * 0.48f, cy + s * 0.04f)
        lineTo(cx + s * 0.56f, cy + s * 0.22f)
        close()
    }
    drawPath(tail, Color(0xFF223377).withA(alpha))
    // blowhole
    ovalAt(cx - s * 0.08f, cy - s * 0.25f, s * 0.04f, s * 0.02f, Color(0xFF112266), alpha)
    // eye
    circleAt(cx - s * 0.22f, cy - s * 0.06f, s * 0.05f, Color.White, alpha)
    circleAt(cx - s * 0.22f, cy - s * 0.06f, s * 0.028f, Color(0xFF112233), alpha)
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 12 : Pieuvre
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawPieuvre(s: Float, alpha: Float) {
    val cx = s * 0.50f; val cy = s * 0.38f
    // 8 tentacles
    val tentacleAngles = listOf(-70f, -40f, -15f, 15f, 40f, 70f, 100f, -100f)
    tentacleAngles.forEach { deg ->
        val rad = Math.toRadians(deg.toDouble()).toFloat()
        val len = s * 0.42f
        val cp1x = cx + cos(rad) * len * 0.4f + sin(rad) * s * 0.12f
        val cp1y = cy + s * 0.22f + sin(rad) * len * 0.4f
        val cp2x = cx + cos(rad) * len * 0.8f - sin(rad) * s * 0.08f
        val cp2y = cy + s * 0.22f + sin(rad) * len * 0.8f
        val ex = cx + cos(rad) * len
        val ey = cy + s * 0.20f + sin(rad) * len
        val p = Path().apply {
            moveTo(cx + cos(rad) * s * 0.16f, cy + s * 0.22f)
            cubicTo(cp1x, cp1y, cp2x, cp2y, ex, ey)
        }
        drawPath(p, Color(0xFFCC4499).withA(alpha), style = Stroke(width = s * 0.055f, pathEffect = PathEffect.cornerPathEffect(s * 0.04f)))
        // suction cup
        circleAt(ex, ey, s * 0.022f, Color(0xFFFFAACC).withA(alpha))
    }
    // mantle (head)
    ovalAt(cx, cy, s * 0.26f, s * 0.22f, Color(0xFFDD55AA), alpha)
    // big yellow eyes
    circleAt(cx - s * 0.10f, cy - s * 0.04f, s * 0.07f, Color(0xFFFFEE00), alpha)
    circleAt(cx + s * 0.10f, cy - s * 0.04f, s * 0.07f, Color(0xFFFFEE00), alpha)
    circleAt(cx - s * 0.10f, cy - s * 0.04f, s * 0.04f, Color.Black, alpha)
    circleAt(cx + s * 0.10f, cy - s * 0.04f, s * 0.04f, Color.Black, alpha)
    // spots
    listOf(Offset(-0.06f, -0.14f), Offset(0.08f, -0.12f), Offset(0.14f, 0.04f)).forEach { (dx, dy) ->
        circleAt(cx + dx * s, cy + dy * s, s * 0.03f, Color(0xFFFF88CC).withA(alpha))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 13 : Espadon
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawEspadon(s: Float, alpha: Float) {
    val cx = s * 0.52f; val cy = s * 0.54f
    // body
    ovalAt(cx, cy, s * 0.38f, s * 0.20f, Color(0xFF336699), alpha)
    // long bill
    val bill = Path().apply {
        moveTo(cx - s * 0.35f, cy - s * 0.02f)
        lineTo(cx - s * 0.90f, cy - s * 0.01f)
        lineTo(cx - s * 0.35f, cy + s * 0.04f)
        close()
    }
    drawPath(bill, Color(0xFF224477).withA(alpha))
    // dorsal fin (sail-like)
    val dorsal = Path().apply {
        moveTo(cx - s * 0.20f, cy - s * 0.18f)
        lineTo(cx - s * 0.05f, cy - s * 0.52f)
        lineTo(cx + s * 0.22f, cy - s * 0.18f)
        close()
    }
    drawPath(dorsal, Color(0xFF5599CC).withA(alpha))
    // pectoral
    val pect = Path().apply {
        moveTo(cx - s * 0.10f, cy + s * 0.04f)
        lineTo(cx - s * 0.28f, cy + s * 0.30f)
        lineTo(cx + s * 0.08f, cy + s * 0.12f)
        close()
    }
    drawPath(pect, Color(0xFF4488BB).withA(alpha))
    // tail (lunate)
    val tail = Path().apply {
        moveTo(cx + s * 0.36f, cy)
        lineTo(cx + s * 0.54f, cy - s * 0.22f)
        lineTo(cx + s * 0.44f, cy)
        lineTo(cx + s * 0.54f, cy + s * 0.22f)
        close()
    }
    drawPath(tail, Color(0xFF4488BB).withA(alpha))
    // eye
    circleAt(cx - s * 0.22f, cy - s * 0.07f, s * 0.055f, Color.White, alpha)
    circleAt(cx - s * 0.22f, cy - s * 0.07f, s * 0.030f, Color.Black, alpha)
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 14 : Tortue Luth
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawTortue(s: Float, alpha: Float) {
    val cx = s * 0.50f; val cy = s * 0.52f
    // shell ridges (5 longitudinal)
    for (i in -2..2) {
        ovalAt(cx + i * s * 0.08f, cy, s * 0.04f, s * 0.34f, Color(0xFF1A1A2E).withA(0.7f * alpha))
    }
    // main shell
    ovalAt(cx, cy, s * 0.36f, s * 0.32f, Color(0xFF223344), alpha)
    // head
    ovalAt(cx - s * 0.34f, cy - s * 0.10f, s * 0.14f, s * 0.12f, Color(0xFF334455), alpha)
    // four flippers
    val flippers = listOf(
        floatArrayOf(cx - s * 0.14f, cy - s * 0.26f, cx - s * 0.40f, cy - s * 0.50f, cx, cy - s * 0.30f),
        floatArrayOf(cx + s * 0.14f, cy - s * 0.22f, cx + s * 0.46f, cy - s * 0.44f, cx + s * 0.24f, cy - s * 0.28f),
        floatArrayOf(cx - s * 0.14f, cy + s * 0.22f, cx - s * 0.44f, cy + s * 0.46f, cx, cy + s * 0.28f),
        floatArrayOf(cx + s * 0.14f, cy + s * 0.22f, cx + s * 0.44f, cy + s * 0.46f, cx + s * 0.22f, cy + s * 0.28f),
    )
    flippers.forEach { f ->
        val p = Path().apply { moveTo(f[0], f[1]); lineTo(f[2], f[3]); lineTo(f[4], f[5]); close() }
        drawPath(p, Color(0xFF334455).withA(alpha))
    }
    // eye
    circleAt(cx - s * 0.40f, cy - s * 0.14f, s * 0.04f, Color(0xFF88AACC), alpha)
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 15 : Grand Requin Blanc
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawGRB(s: Float, alpha: Float) {
    val cx = s * 0.50f; val cy = s * 0.54f
    // body torpedo
    ovalAt(cx, cy, s * 0.42f, s * 0.24f, Color(0xFF778899), alpha)
    // white belly
    ovalAt(cx + s * 0.04f, cy + s * 0.08f, s * 0.32f, s * 0.14f, Color(0xFFEEEEEE), alpha)
    // dorsal fin
    val dorsal = Path().apply {
        moveTo(cx - s * 0.04f, cy - s * 0.22f)
        lineTo(cx + s * 0.04f, cy - s * 0.50f)
        lineTo(cx + s * 0.18f, cy - s * 0.22f)
        close()
    }
    drawPath(dorsal, Color(0xFF667788).withA(alpha))
    // pectorals
    val pect = Path().apply {
        moveTo(cx - s * 0.08f, cy + s * 0.04f)
        lineTo(cx - s * 0.36f, cy + s * 0.28f)
        lineTo(cx + s * 0.08f, cy + s * 0.10f)
        close()
    }
    drawPath(pect, Color(0xFF6688AA).withA(alpha))
    // lunate tail
    val tail = Path().apply {
        moveTo(cx + s * 0.38f, cy)
        lineTo(cx + s * 0.56f, cy - s * 0.24f)
        lineTo(cx + s * 0.46f, cy)
        lineTo(cx + s * 0.56f, cy + s * 0.20f)
        close()
    }
    drawPath(tail, Color(0xFF667788).withA(alpha))
    // cold dark eye
    circleAt(cx - s * 0.24f, cy - s * 0.08f, s * 0.055f, Color(0xFF222222), alpha)
    circleAt(cx - s * 0.24f, cy - s * 0.08f, s * 0.025f, Color.Black, alpha)
    // grin (teeth)
    val mouth = Path().apply {
        moveTo(cx - s * 0.38f, cy + s * 0.04f)
        lineTo(cx - s * 0.20f, cy + s * 0.08f)
    }
    drawPath(mouth, Color(0xFF334455).withA(alpha), style = Stroke(width = s * 0.02f))
    for (i in 0..3) {
        val tx = cx - s * 0.36f + i * s * 0.055f
        drawLine(Color.White.withA(alpha), Offset(tx, cy + s * 0.04f), Offset(tx, cy + s * 0.10f), strokeWidth = s * 0.018f)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 16 : Mégalodon
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawMegalodont(s: Float, alpha: Float) {
    val cx = s * 0.50f; val cy = s * 0.54f
    ovalAt(cx, cy, s * 0.46f, s * 0.28f, Color(0xFF445566), alpha)
    ovalAt(cx + s * 0.06f, cy + s * 0.10f, s * 0.34f, s * 0.15f, Color(0xFFCCCCCC), alpha)
    // massive jaw
    val jaw = Path().apply {
        moveTo(cx - s * 0.44f, cy + s * 0.06f)
        lineTo(cx - s * 0.28f, cy + s * 0.22f)
        lineTo(cx + s * 0.10f, cy + s * 0.22f)
        lineTo(cx + s * 0.18f, cy + s * 0.06f)
        close()
    }
    drawPath(jaw, Color(0xFF334455).withA(alpha))
    // huge teeth
    for (i in 0..5) {
        val tx = cx - s * 0.38f + i * s * 0.10f
        val h = if (i % 2 == 0) s * 0.14f else s * 0.10f
        val tooth = Path().apply {
            moveTo(tx, cy + s * 0.06f)
            lineTo(tx + s * 0.045f, cy + s * 0.06f + h)
            lineTo(tx + s * 0.09f, cy + s * 0.06f)
        }
        drawPath(tooth, Color.White.withA(alpha))
    }
    // dorsal fin
    val dorsal = Path().apply {
        moveTo(cx - s * 0.08f, cy - s * 0.26f)
        lineTo(cx + s * 0.02f, cy - s * 0.58f)
        lineTo(cx + s * 0.22f, cy - s * 0.26f)
        close()
    }
    drawPath(dorsal, Color(0xFF334455).withA(alpha))
    // pectorals
    val pect = Path().apply {
        moveTo(cx - s * 0.10f, cy + s * 0.06f)
        lineTo(cx - s * 0.40f, cy + s * 0.34f)
        lineTo(cx + s * 0.08f, cy + s * 0.14f)
        close()
    }
    drawPath(pect, Color(0xFF3A4A5A).withA(alpha))
    // glowing orange eyes
    circleAt(cx - s * 0.24f, cy - s * 0.10f, s * 0.07f, Color(0xFFFF6600), alpha)
    circleAt(cx - s * 0.24f, cy - s * 0.10f, s * 0.04f, Color.Black, alpha)
    // tail
    val tail = Path().apply {
        moveTo(cx + s * 0.42f, cy)
        lineTo(cx + s * 0.58f, cy - s * 0.26f)
        lineTo(cx + s * 0.48f, cy)
        lineTo(cx + s * 0.58f, cy + s * 0.22f)
        close()
    }
    drawPath(tail, Color(0xFF334455).withA(alpha))
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 17 : Capibara
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawCapibara(s: Float, alpha: Float) {
    val cx = s * 0.50f; val cy = s * 0.58f
    // body
    ovalAt(cx, cy, s * 0.36f, s * 0.26f, Color(0xFFAA8855), alpha)
    // legs (4 stumps)
    listOf(-0.20f, -0.06f, 0.08f, 0.22f).forEach { dx ->
        ovalAt(cx + dx * s, cy + s * 0.28f, s * 0.06f, s * 0.10f, Color(0xFF997744), alpha)
    }
    // head
    ovalAt(cx - s * 0.30f, cy - s * 0.10f, s * 0.20f, s * 0.17f, Color(0xFFBB9966), alpha)
    // nose
    ovalAt(cx - s * 0.44f, cy - s * 0.04f, s * 0.10f, s * 0.07f, Color(0xFF997755), alpha)
    // nostrils
    circleAt(cx - s * 0.48f, cy - s * 0.06f, s * 0.022f, Color(0xFF664433), alpha)
    circleAt(cx - s * 0.40f, cy - s * 0.06f, s * 0.022f, Color(0xFF664433), alpha)
    // flower crown
    val flowerAngles = listOf(0f, 60f, 120f, 180f, 240f, 300f)
    val crownCx = cx - s * 0.30f; val crownCy = cy - s * 0.30f
    flowerAngles.forEach { deg ->
        val rad = Math.toRadians(deg.toDouble()).toFloat()
        val fx = crownCx + cos(rad) * s * 0.10f
        val fy = crownCy + sin(rad) * s * 0.08f
        circleAt(fx, fy, s * 0.04f, Color(0xFFFF6688), alpha)
    }
    circleAt(crownCx, crownCy, s * 0.04f, Color(0xFFFFDD00), alpha)
    // water drops
    listOf(Offset(0.14f, -0.24f), Offset(0.24f, -0.12f)).forEach { (dx, dy) ->
        val p = Path().apply {
            moveTo(cx + dx * s, cy + dy * s - s * 0.06f)
            cubicTo(cx + dx * s - s * 0.04f, cy + dy * s, cx + dx * s + s * 0.04f, cy + dy * s, cx + dx * s, cy + dy * s - s * 0.06f)
        }
        drawPath(p, Color(0xFF44AAFF).withA(0.8f * alpha))
    }
    // eye
    circleAt(cx - s * 0.34f, cy - s * 0.14f, s * 0.04f, Color(0xFF442211), alpha)
    // ear
    ovalAt(cx - s * 0.20f, cy - s * 0.26f, s * 0.05f, s * 0.07f, Color(0xFFBB9966), alpha)
    ovalAt(cx - s * 0.20f, cy - s * 0.26f, s * 0.03f, s * 0.05f, Color(0xFFFFCCBB), alpha)
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 18 : Léviathan
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawLeviathan(s: Float, alpha: Float) {
    val cx = s * 0.50f; val cy = s * 0.50f
    // main serpentine body
    val body = Path().apply {
        moveTo(cx - s * 0.48f, cy + s * 0.20f)
        cubicTo(cx - s * 0.30f, cy - s * 0.40f, cx + s * 0.30f, cy + s * 0.40f, cx + s * 0.48f, cy - s * 0.20f)
    }
    drawPath(body, Color(0xFF0A0A2E).withA(alpha), style = Stroke(width = s * 0.22f, cap = StrokeCap.Round))
    // bioluminescent dots
    val dotPositions = listOf(
        Offset(cx - s * 0.30f, cy + s * 0.05f), Offset(cx - s * 0.10f, cy + s * 0.12f),
        Offset(cx + s * 0.10f, cy + s * 0.05f), Offset(cx + s * 0.28f, cy - s * 0.06f),
        Offset(cx - s * 0.40f, cy + s * 0.14f), Offset(cx + s * 0.40f, cy - s * 0.12f),
    )
    dotPositions.forEach { circleAt(it.x, it.y, s * 0.025f, Color(0xFF00FFAA).withA(0.9f * alpha)) }
    // head
    ovalAt(cx - s * 0.36f, cy + s * 0.26f, s * 0.18f, s * 0.14f, Color(0xFF0A0A2E), alpha)
    // terrifying red eyes
    circleAt(cx - s * 0.44f, cy + s * 0.22f, s * 0.06f, Color(0xFFFF0000), alpha)
    circleAt(cx - s * 0.28f, cy + s * 0.22f, s * 0.06f, Color(0xFFFF0000), alpha)
    circleAt(cx - s * 0.44f, cy + s * 0.22f, s * 0.032f, Color.Black, alpha)
    circleAt(cx - s * 0.28f, cy + s * 0.22f, s * 0.032f, Color.Black, alpha)
    // tentacle tip
    val tip = Path().apply {
        moveTo(cx + s * 0.46f, cy - s * 0.20f)
        quadraticTo(cx + s * 0.60f, cy - s * 0.08f, cx + s * 0.56f, cy + s * 0.10f)
    }
    drawPath(tip, Color(0xFF0A0A2E).withA(alpha), style = Stroke(width = s * 0.08f, cap = StrokeCap.Round))
}

// ─────────────────────────────────────────────────────────────────────────────
// Level 19 : Poséidon
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawPoseidon(s: Float, alpha: Float) {
    val cx = s * 0.50f; val cy = s * 0.54f
    // divine glow aura
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFFFFDD00).withA(0.35f * alpha), Color.Transparent),
            center = Offset(cx, cy * 0.70f),
            radius = s * 0.55f,
        ),
        radius = s * 0.55f,
        center = Offset(cx, cy * 0.70f),
    )
    // flowing robe / body
    val robe = Path().apply {
        moveTo(cx - s * 0.22f, cy - s * 0.18f)
        cubicTo(cx - s * 0.32f, cy + s * 0.18f, cx - s * 0.44f, cy + s * 0.44f, cx, cy + s * 0.50f)
        cubicTo(cx + s * 0.44f, cy + s * 0.44f, cx + s * 0.32f, cy + s * 0.18f, cx + s * 0.22f, cy - s * 0.18f)
        close()
    }
    drawPath(robe, Color(0xFF2244AA).withA(alpha))
    // scales
    for (row in 0..3) for (col in 0..3) {
        val sx2 = cx - s * 0.18f + col * s * 0.12f
        val sy = cy + row * s * 0.10f
        drawArc(Color(0xFF3355BB).withA(0.7f * alpha), 0f, 180f, false,
            Offset(sx2, sy).let { Rect(it.x - s * 0.065f, it.y - s * 0.04f, it.x + s * 0.065f, it.y + s * 0.04f) })
    }
    // torso
    ovalAt(cx, cy - s * 0.10f, s * 0.20f, s * 0.24f, Color(0xFF99AACC), alpha)
    // gold crown
    val crownPts = listOf(
        floatArrayOf(cx - s * 0.18f, cy - s * 0.36f),
        floatArrayOf(cx - s * 0.10f, cy - s * 0.54f),
        floatArrayOf(cx, cy - s * 0.44f),
        floatArrayOf(cx + s * 0.10f, cy - s * 0.54f),
        floatArrayOf(cx + s * 0.18f, cy - s * 0.36f),
    )
    val crown = Path().apply {
        moveTo(cx - s * 0.22f, cy - s * 0.32f)
        crownPts.forEach { lineTo(it[0], it[1]) }
        lineTo(cx + s * 0.22f, cy - s * 0.32f)
        close()
    }
    drawPath(crown, Color(0xFFFFCC00).withA(alpha))
    // crown gems
    listOf(-0.10f, 0f, 0.10f).zip(listOf(Color(0xFFFF4444), Color(0xFF44FFAA), Color(0xFF4488FF))).forEach { (dx, c) ->
        circleAt(cx + dx * s, cy - s * 0.38f, s * 0.028f, c.withA(alpha))
    }
    // head
    ovalAt(cx, cy - s * 0.30f, s * 0.16f, s * 0.14f, Color(0xFFDDBB99), alpha)
    // beard
    val beard = Path().apply {
        moveTo(cx - s * 0.10f, cy - s * 0.18f)
        cubicTo(cx - s * 0.16f, cy - s * 0.04f, cx + s * 0.16f, cy - s * 0.04f, cx + s * 0.10f, cy - s * 0.18f)
    }
    drawPath(beard, Color(0xFFEEDDAA).withA(alpha), style = Stroke(width = s * 0.04f, cap = StrokeCap.Round))
    // eyes
    circleAt(cx - s * 0.06f, cy - s * 0.32f, s * 0.04f, Color(0xFF4488FF), alpha)
    circleAt(cx + s * 0.06f, cy - s * 0.32f, s * 0.04f, Color(0xFF4488FF), alpha)
    // trident
    val tx = cx + s * 0.34f; val ty = cy - s * 0.12f
    drawLine(Color(0xFFFFCC00).withA(alpha), Offset(tx, ty + s * 0.50f), Offset(tx, ty - s * 0.40f), strokeWidth = s * 0.025f)
    listOf(-0.04f, 0f, 0.04f).forEach { dx ->
        drawLine(Color(0xFFFFCC00).withA(alpha), Offset(tx + dx * s, ty - s * 0.28f), Offset(tx + dx * s, ty - s * 0.40f), strokeWidth = s * 0.02f)
    }
    // sea foam / bubbles
    listOf(Offset(-0.36f, 0.38f), Offset(-0.26f, 0.48f), Offset(-0.14f, 0.42f)).forEach { (dx, dy) ->
        drawCircle(Color.White.withA(0.5f * alpha), s * 0.022f, Offset(cx + dx * s, cy + dy * s))
    }
}

private fun DrawScope.drawArc(color: Color, startAngle: Float, sweepAngle: Float, useCenter: Boolean, oval: Rect) {
    drawArc(color, startAngle, sweepAngle, useCenter, topLeft = oval.topLeft, size = oval.size)
}
