package com.multipoisson.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.material3.Text

private data class Step(val label: String, val bg: Color)

private val STEPS = listOf(
    Step("3", Color(0xFFFF9600)),
    Step("2", Color(0xFF9B5DE5)),
    Step("1", Color(0xFF1CB0F6)),
    Step("GO !", Color(0xFF58CC02)),
)

@Composable
fun CountdownScreen(onDone: () -> Unit) {
    var stepIndex by remember { mutableIntStateOf(0) }
    val scale = remember { Animatable(0.4f) }

    LaunchedEffect(Unit) {
        for (i in STEPS.indices) {
            stepIndex = i
            scale.snapTo(0.4f)
            scale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 260f))
            delay(if (i < STEPS.size - 1) 500L else 650L)
        }
        onDone()
    }

    val step = STEPS[stepIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(step.bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = step.label,
            fontSize = if (step.label.length > 1) 96.sp else 120.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.scale(scale.value),
        )
    }
}
