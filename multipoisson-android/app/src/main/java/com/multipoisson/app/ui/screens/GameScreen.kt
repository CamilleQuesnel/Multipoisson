package com.multipoisson.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multipoisson.app.logic.*
import com.multipoisson.app.ui.components.NumericKeypad
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.GameViewModel
import kotlinx.coroutines.delay

private const val FEEDBACK_CORRECT_MS = 2_000L

private sealed class Phase {
    object Playing : Phase()
    data class FeedbackCorrect(val bonus: Int) : Phase()
    object FeedbackWrong : Phase()
}

@Composable
fun GameScreen(
    gameViewModel: GameViewModel,
    onFinish: () -> Unit,
) {
    val config = gameViewModel.config ?: run {
        Box(Modifier.fillMaxSize().background(AppColors.Background), contentAlignment = Alignment.Center) {
            Text("Erreur : configuration manquante.", color = AppColors.TextSecondary, fontSize = 18.sp)
        }
        return
    }

    val questions = remember { generateQuestions(config) }

    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize().background(AppColors.Background), contentAlignment = Alignment.Center) {
            Text(
                "Aucune question disponible.\nModifie les réglages !",
                textAlign = TextAlign.Center,
                color = AppColors.TextSecondary,
                fontSize = 18.sp,
                modifier = Modifier.padding(32.dp),
            )
        }
        return
    }

    var currentIndex   by remember { mutableIntStateOf(0) }
    var inputValue     by remember { mutableStateOf("") }
    var correctCount   by remember { mutableIntStateOf(0) }
    var bonusPoints    by remember { mutableIntStateOf(0) }
    var phase          by remember { mutableStateOf<Phase>(Phase.Playing) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var timerPaused    by remember { mutableStateOf(false) }
    val questionStartTime = remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Global timer
    LaunchedEffect(Unit) {
        if (!config.timerEnabled) return@LaunchedEffect
        while (true) {
            delay(1_000L)
            if (!timerPaused) elapsedSeconds++
        }
    }

    // Reset question start time
    LaunchedEffect(currentIndex, phase) {
        if (phase is Phase.Playing) {
            questionStartTime.longValue = System.currentTimeMillis()
        }
    }

    // Helper to advance to next question or finish
    fun advance() {
        timerPaused = false
        val next = currentIndex + 1
        if (next >= questions.size) {
            gameViewModel.finishGame(
                score          = correctCount * 5,
                maxScore       = questions.size * 5,
                bonusPoints    = bonusPoints,
                elapsedSeconds = if (config.timerEnabled) elapsedSeconds else null,
            )
            onFinish()
        } else {
            currentIndex = next
            inputValue = ""
            phase = Phase.Playing
        }
    }

    // Correct: auto-advance after 2s. Wrong: wait for tap (advance() called on click)
    LaunchedEffect(phase) {
        if (phase !is Phase.FeedbackCorrect) return@LaunchedEffect
        timerPaused = true
        delay(FEEDBACK_CORRECT_MS)
        advance()
    }
    // Pause timer immediately on wrong answer (resume happens in advance())
    LaunchedEffect(phase) {
        if (phase is Phase.FeedbackWrong) timerPaused = true
    }

    val question = questions[currentIndex]
    val progress = currentIndex.toFloat() / questions.size
    val displayScore = correctCount * 5 + bonusPoints

    // Question scale animation
    val questionScale = remember { Animatable(0.85f) }
    LaunchedEffect(currentIndex) {
        questionScale.snapTo(0.85f)
        questionScale.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 320f))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── TOP BAR ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InfoChip("⭐ $displayScore", AppColors.YellowLight, AppColors.Yellow, Color(0xFF8B6800))
                if (config.timerEnabled) {
                    InfoChip("⏱ ${formatTime(elapsedSeconds)}", AppColors.BlueLight, AppColors.Blue, AppColors.BlueDark)
                }
                InfoChip("${currentIndex + 1} / ${questions.size}", AppColors.BlueLight, AppColors.Blue, AppColors.BlueDark)
            }

            // ── PROGRESS BAR ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(12.dp)
                    .background(AppColors.Border, RoundedCornerShape(6.dp)),
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(400),
                    label = "progress",
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            Brush.horizontalGradient(listOf(AppColors.Green, Color(0xFF88EE22))),
                            RoundedCornerShape(6.dp),
                        ),
                )
            }

            // ── QUESTION ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .scale(questionScale.value),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(
                        text = "${question.a} × ${question.b} = ?",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.Blue,
                        textAlign = TextAlign.Center,
                    )
                    InputDisplay(value = inputValue)
                }
            }

            // ── KEYPAD ─────────────────────────────────────────────────
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                NumericKeypad(
                    value = inputValue,
                    onChange = { inputValue = it },
                    onValidate = {
                        if (phase == Phase.Playing && inputValue.isNotEmpty()) {
                            val userAnswer = inputValue.toIntOrNull() ?: return@NumericKeypad
                            val timeTaken = System.currentTimeMillis() - questionStartTime.longValue
                            val isCorrect = userAnswer == question.answer
                            gameViewModel.recordQuestion(
                                tableN        = question.a,
                                operandA      = question.a,
                                operandB      = question.b,
                                isCorrect     = isCorrect,
                                responseTimeMs = timeTaken,
                            )
                            if (isCorrect) {
                                val bonus = getBonusForTime(timeTaken, config.timerEnabled)
                                if (bonus > 0) bonusPoints += bonus
                                correctCount++
                                phase = Phase.FeedbackCorrect(bonus)
                            } else {
                                phase = Phase.FeedbackWrong
                            }
                        }
                    },
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        // ── FEEDBACK OVERLAY ───────────────────────────────────────────
        if (phase != Phase.Playing) {
            val feedbackScale = remember { Animatable(0f) }
            LaunchedEffect(phase) {
                feedbackScale.snapTo(0f)
                feedbackScale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 280f))
            }
            val isWrong = phase is Phase.FeedbackWrong

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(feedbackScale.value)
                    .background(
                        if (!isWrong)
                            Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)))
                        else
                            Brush.verticalGradient(listOf(Color(0xFFFF7043), Color(0xFFE64A19)))
                    )
                    .then(
                        if (isWrong) Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { advance() },
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(32.dp),
                ) {
                    if (phase is Phase.FeedbackCorrect) {
                        Text("✓", fontSize = 90.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("Bravo !", fontSize = 44.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("+5 points ⭐", fontSize = 20.sp, color = Color.White.copy(0.9f), fontWeight = FontWeight.Bold)
                        val bonus = (phase as Phase.FeedbackCorrect).bonus
                        if (bonus > 0) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(14.dp),
                            ) {
                                Text(
                                    getBonusLabel(bonus),
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AppColors.Yellow,
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                                )
                            }
                        }
                    } else {
                        Text("✗", fontSize = 90.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("Pas tout à fait...", fontSize = 30.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Surface(
                            color = Color.Black.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Text(
                                "${question.a} × ${question.b} = ${question.answer}",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                            )
                        }
                        Text("Retiens bien ! 🧠", fontSize = 20.sp, color = Color.White.copy(0.9f), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "Tape pour continuer →",
                            fontSize = 16.sp,
                            color = Color.White.copy(0.65f),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InputDisplay(value: String) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(64.dp)
            .shadow(6.dp, RoundedCornerShape(18.dp))
            .background(AppColors.White, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.ifEmpty { "…" },
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = if (value.isEmpty()) AppColors.Border else AppColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun InfoChip(label: String, bg: Color, borderColor: Color, text: Color) {
    Surface(
        color = bg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
    ) {
        Text(
            label,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
    }
}
