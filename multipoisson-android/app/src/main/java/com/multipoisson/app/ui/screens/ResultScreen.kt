package com.multipoisson.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multipoisson.app.logic.formatTime
import com.multipoisson.app.navigation.Screen
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(
    gameViewModel: GameViewModel,
    onReplay: () -> Unit,
) {
    val isSaving = gameViewModel.isSaving
    val result   = gameViewModel.gameResult
    val newFish  = gameViewModel.newlyUnlockedFish

    // Still saving — show a simple loading screen
    if (isSaving || result == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CircularProgressIndicator(color = AppColors.Blue)
                Text("Enregistrement…", color = AppColors.TextSecondary, fontSize = 16.sp)
            }
        }
        return
    }

    val score        = result.score
    val maxScore     = result.maxScore
    val bonusPoints  = result.bonusPoints
    val total        = score + bonusPoints
    val percentage   = if (maxScore > 0) (score * 100 / maxScore) else 0
    val timerEnabled = result.timeSeconds != null

    val scoreColor = when {
        percentage >= 80 -> AppColors.Green
        percentage >= 50 -> AppColors.Orange
        else             -> AppColors.Red
    }

    // Score card entrance
    val cardScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(150)
        cardScale.animateTo(1.1f, spring(dampingRatio = 0.4f, stiffness = 280f))
        cardScale.animateTo(1f, spring(dampingRatio = 0.7f))
    }

    // Animated score counter
    var displayedScore by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        delay(400)
        val steps = 20
        repeat(steps) { i ->
            displayedScore = (total * (i + 1) / steps)
            delay(30)
        }
        displayedScore = total
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to AppColors.BlueLight,
                    0.3f to AppColors.Background,
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))

        Text(
            "Bravo ! 🎉",
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        // Mascot placeholder (Bloc D apportera l'image réelle)
        Text(
            text = when {
                percentage >= 100 -> "🏆"
                percentage >= 80  -> "🌟"
                percentage >= 50  -> "👍"
                else              -> "💪"
            },
            fontSize = 96.sp,
        )

        Spacer(Modifier.height(16.dp))

        // ── Score card ─────────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .scale(cardScale.value),
            shape = RoundedCornerShape(28.dp),
            color = AppColors.White,
            shadowElevation = 10.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Ton score", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary)

                Text(
                    "$displayedScore / $maxScore",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                    color = scoreColor,
                    lineHeight = 60.sp,
                )

                if (timerEnabled && bonusPoints > 0) {
                    Surface(
                        color = AppColors.Orange.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(
                            "$score pts + $bonusPoints bonus ⚡",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Orange,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        )
                    }
                }

                Text("$percentage%", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.TextSecondary)

                // Stars
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..5).forEach { i ->
                        val lit = percentage >= i * 20
                        val starScale by animateFloatAsState(
                            targetValue = if (lit) 1f else 0.75f,
                            animationSpec = tween(300, delayMillis = i * 80),
                            label = "star$i",
                        )
                        Text(
                            "⭐",
                            fontSize = 30.sp,
                            modifier = Modifier.scale(starScale),
                            color = if (lit) Color.Unspecified else Color.Gray.copy(alpha = 0.25f),
                        )
                    }
                }

                if (timerEnabled && result.timeSeconds != null) {
                    Surface(
                        color = AppColors.BlueLight,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            "⏱ Temps : ${formatTime(result.timeSeconds)}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.BlueDark,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                }
            }
        }

        // ── Newly unlocked fish ────────────────────────────────────────────
        if (newFish.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = AppColors.White,
                shadowElevation = 6.dp,
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "🐟 Nouveau(x) poisson(s) débloqué(s) !",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.Blue,
                    )
                    newFish.forEach { fishId ->
                        Text(
                            "• $fishId",
                            fontSize = 15.sp,
                            color = AppColors.TextPrimary,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Action buttons ─────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionButton(
                label = "🔄 Rejouer",
                containerColor = AppColors.Green,
                modifier = Modifier.weight(1f),
                onClick = {
                    gameViewModel.reset()
                    onReplay()
                },
            )
            ActionButton(
                label = "👤 Changer",
                containerColor = AppColors.Blue,
                modifier = Modifier.weight(1f),
                onClick = onReplay,
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ActionButton(label: String, containerColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
    ) {
        Text(label, fontSize = 17.sp, fontWeight = FontWeight.Black, color = Color.White)
    }
}
