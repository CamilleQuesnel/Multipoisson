package com.multipoisson.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.multipoisson.app.model.GameMode
import com.multipoisson.app.navigation.Screen
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.GameViewModel

@Composable
fun JouerTab(navController: NavController, gameViewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "🐟",
            fontSize = 52.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Quel mode ?",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = AppColors.TextPrimary,
        )
        Text(
            "Choisis comment tu veux t'entraîner",
            fontSize = 14.sp,
            color = AppColors.TextSecondary,
        )

        Spacer(Modifier.height(32.dp))

        // ── Tables ────────────────────────────────────────────────────────
        ModeCard(
            emoji = "📚",
            title = "Tables",
            description = "Choisis tes tables\net travaille-les !",
            gradientStart = Color(0xFF3B82F6),
            gradientEnd   = Color(0xFF1D4ED8),
            badge = null,
            onClick = {
                gameViewModel.setMode(GameMode.TABLE)
                navController.navigate(Screen.TableSelection.route)
            },
        )

        Spacer(Modifier.height(14.dp))

        // ── Mixte ─────────────────────────────────────────────────────────
        ModeCard(
            emoji = "🔀",
            title = "Mixte",
            description = "Toutes les tables\nmélangées !",
            gradientStart = Color(0xFF10B981),
            gradientEnd   = Color(0xFF059669),
            badge = null,
            onClick = {
                gameViewModel.setMode(GameMode.MIXTE)
                gameViewModel.setSelectedTables((2..9).toList())
                navController.navigate(Screen.AdvancedConfig.route)
            },
        )

        Spacer(Modifier.height(14.dp))

        // ── Défi ─────────────────────────────────────────────────────────
        ModeCard(
            emoji = "⚡",
            title = "Défi",
            description = "Course contre\nla montre !",
            gradientStart = Color(0xFFF59E0B),
            gradientEnd   = Color(0xFFD97706),
            badge = "Chrono",
            onClick = {
                gameViewModel.setMode(GameMode.DEFI)
                gameViewModel.setSelectedTables((2..9).toList())
                navController.navigate(Screen.AdvancedConfig.route)
            },
        )
    }
}

@Composable
private fun ModeCard(
    emoji: String,
    title: String,
    description: String,
    gradientStart: Color,
    gradientEnd: Color,
    badge: String?,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(gradientStart, gradientEnd)))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Big emoji icon
            Text(emoji, fontSize = 44.sp)

            // Text block
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                    )
                    if (badge != null) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.25f),
                        ) {
                            Text(
                                badge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
                Text(
                    description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 18.sp,
                )
            }

            // Arrow
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
