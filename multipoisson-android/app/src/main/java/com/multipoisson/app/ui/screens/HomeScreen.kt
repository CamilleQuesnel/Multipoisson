package com.multipoisson.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multipoisson.app.data.GameStorage
import com.multipoisson.app.ui.components.FishMascot
import com.multipoisson.app.ui.theme.AppColors

@Composable
fun HomeScreen(
    onStart: (playerName: String, tables: List<Int>) -> Unit,
) {
    val context = LocalContext.current
    var playerName by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        playerName = GameStorage.getLastPlayerName(context)
    }

    val canStart = playerName.isNotBlank()

    // Subtle entrance animation for the card
    val cardAlpha = remember { Animatable(0f) }
    val cardOffset = remember { Animatable(40f) }
    LaunchedEffect(Unit) {
        cardAlpha.animateTo(1f, tween(500, delayMillis = 200))
        cardOffset.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 180f))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to AppColors.BlueLight,
                    0.45f to AppColors.Background,
                    1f to AppColors.Background,
                )
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Title
            Text(
                text = "MultiPoisson",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = AppColors.Blue,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Tables de multiplication",
                fontSize = 16.sp,
                color = AppColors.TextSecondary,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(28.dp))

            FishMascot(level = 1, size = 150.dp, animate = true)

            Spacer(Modifier.height(28.dp))

            // Name card with entrance animation
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = cardOffset.value.dp),
                shape = RoundedCornerShape(28.dp),
                color = AppColors.White,
                shadowElevation = 10.dp,
                tonalElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        "Qui joue ?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextPrimary,
                    )
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = { playerName = it.take(20) },
                        placeholder = { Text("Ton prénom…", color = AppColors.TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.Blue,
                            unfocusedBorderColor = AppColors.Border,
                            focusedLabelColor = AppColors.Blue,
                        ),
                    )
                    Button(
                        onClick = {
                            if (canStart) {
                                keyboard?.hide()
                                GameStorage.saveLastPlayerName(context, playerName.trim())
                                onStart(playerName.trim(), emptyList())
                            }
                        },
                        enabled = canStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Green,
                            disabledContainerColor = AppColors.Border,
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp,
                        ),
                    ) {
                        Text(
                            "Jouer !",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = AppColors.White,
                        )
                    }
                }
            }
        }
    }
}
