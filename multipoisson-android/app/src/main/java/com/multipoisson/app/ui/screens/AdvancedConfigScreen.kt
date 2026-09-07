package com.multipoisson.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multipoisson.app.model.GameConfig
import com.multipoisson.app.model.MAX_QUESTION_COUNT
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.GameViewModel

@Composable
fun AdvancedConfigScreen(
    gameViewModel: GameViewModel,
    onStart: () -> Unit,
) {
    val selectedTables = gameViewModel.selectedTables

    var excludeZero by remember { mutableStateOf(false) }
    var excludeOne  by remember { mutableStateOf(false) }
    var excludeTen  by remember { mutableStateOf(false) }
    var timerEnabled by remember { mutableStateOf(false) }
    var questionCount by remember { mutableIntStateOf(20) }

    val maxPool = run {
        val range = (0..10).count { m ->
            !(excludeZero && m == 0) && !(excludeOne && m == 1) && !(excludeTen && m == 10)
        }
        selectedTables.size * range
    }
    val clampedCount = questionCount.coerceIn(1, MAX_QUESTION_COUNT)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(48.dp))
            Text(
                "Réglages",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(20.dp))

            // Exclusions card
            ConfigCard(title = "Exclure des facteurs") {
                ToggleRow("Exclure le × 0", excludeZero) { excludeZero = it }
                ToggleRow("Exclure le × 1", excludeOne)  { excludeOne  = it }
                ToggleRow("Exclure le × 10", excludeTen) { excludeTen  = it }
            }

            Spacer(Modifier.height(12.dp))

            // Timer card
            ConfigCard(title = "Chronomètre") {
                ToggleRow("Activer le chrono (bonus de vitesse)", timerEnabled) { timerEnabled = it }
                if (timerEnabled) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "< 5s → +5 pts  |  < 10s → +3 pts  |  < 20s → +1 pt",
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Question count card
            ConfigCard(title = "Nombre de questions") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CounterButton("-") { if (questionCount > 1) questionCount -= if (questionCount > 50) 10 else 1 }
                    Text(
                        "$clampedCount",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.Blue,
                    )
                    CounterButton("+") {
                        val step = if (questionCount >= 50) 10 else 1
                        questionCount = (questionCount + step).coerceAtMost(MAX_QUESTION_COUNT)
                    }
                }
                if (maxPool > 0 && clampedCount > maxPool) {
                    Text(
                        "⚠️ Les questions seront répétées (pool = $maxPool)",
                        fontSize = 12.sp,
                        color = AppColors.Orange,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                // Quick presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    listOf(10, 20, 50, 100).forEach { preset ->
                        OutlinedButton(
                            onClick = { questionCount = preset },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (clampedCount == preset) AppColors.Blue else AppColors.TextSecondary,
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                        ) {
                            Text("$preset", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        // Start button pinned at bottom
        Box(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = {
                    gameViewModel.prepareGame(
                        GameConfig(
                            selectedTables = selectedTables,
                            excludeZero = excludeZero,
                            excludeOne = excludeOne,
                            excludeTen = excludeTen,
                            timerEnabled = timerEnabled,
                            questionCount = clampedCount,
                        )
                    )
                    onStart()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
            ) {
                Text("C'est parti !", fontSize = 20.sp, fontWeight = FontWeight.Black, color = AppColors.White)
            }
        }
    }
}

@Composable
private fun ConfigCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = AppColors.White,
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
            HorizontalDivider(color = AppColors.Border)
            content()
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 15.sp, color = AppColors.TextPrimary, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = AppColors.White, checkedTrackColor = AppColors.Blue),
        )
    }
}

@Composable
private fun CounterButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(48.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Blue),
    ) {
        Text(label, fontSize = 24.sp, fontWeight = FontWeight.Black, color = AppColors.White)
    }
}
