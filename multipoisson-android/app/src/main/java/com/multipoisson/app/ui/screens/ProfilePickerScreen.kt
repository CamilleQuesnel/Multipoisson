package com.multipoisson.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import com.multipoisson.app.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multipoisson.app.data.repository.ProfileRepository
import com.multipoisson.app.navigation.Screen
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.ProfileViewModel

/** Colors cycling across profile slots (index 0–4). */
private val PROFILE_COLORS = listOf(
    AppColors.Blue,
    AppColors.Green,
    AppColors.Orange,
    AppColors.Purple,
    Color(0xFFE91E8C), // rose vif
)

@Composable
fun ProfilePickerScreen(navController: NavController) {
    val profileViewModel: ProfileViewModel = viewModel()
    val profiles by profileViewModel.profiles.collectAsState()
    val canAddProfile = profiles.size < 5

    // ── Bubble animations ──────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "bubbles")
    val bubbles = listOf(
        Triple(0.18f, 12.dp, 4800),
        Triple(0.55f, 8.dp,  6200),
        Triple(0.80f, 16.dp, 5400),
        Triple(0.35f, 10.dp, 7100),
    )
    val bubbleOffsets = bubbles.mapIndexed { i, (_, _, dur) ->
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = -0.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(dur, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(i * 1200),
            ),
            label = "bubble$i",
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to Color(0xFF5BC8F5),
                    0.5f to AppColors.Blue,
                    1f to AppColors.BlueDark,
                )
            ),
    ) {
        // ── Floating bubbles (decorative) ──────────────────────────────────
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenH = maxHeight
            bubbles.forEachIndexed { i, (xFraction, size, _) ->
                val yOffset = screenH * bubbleOffsets[i].value
                Box(
                    modifier = Modifier
                        .offset(x = maxWidth * xFraction, y = screenH + yOffset)
                        .size(size)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                )
            }
        }

        // ── Main content ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Gear icon row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = { navController.navigate(Screen.Parents.route) }) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Espace parents",
                        tint = Color.White.copy(alpha = 0.85f),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "MultiPoisson",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            Text(
                "Qui est là ?",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(20.dp))

            // Mascotte flottante avec clignement
            val mascotTransition = rememberInfiniteTransition(label = "mascotFloat")
            val mascotY by mascotTransition.animateFloat(
                initialValue = 0f,
                targetValue = -14f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "mascotY",
            )
            // Blink: scaleY flashes to 0.82 for ~110ms every ~3.5s
            val blinkScaleY = remember { Animatable(1f) }
            LaunchedEffect(Unit) {
                while (true) {
                    kotlinx.coroutines.delay(3500L)
                    blinkScaleY.animateTo(0.82f, tween(55, easing = FastOutSlowInEasing))
                    blinkScaleY.animateTo(1f,    tween(110, easing = LinearOutSlowInEasing))
                }
            }
            Image(
                painter = painterResource(R.drawable.mascot_base),
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .offset(y = mascotY.dp)
                    .graphicsLayer { scaleY = blinkScaleY.value },
            )

            Spacer(Modifier.height(32.dp))

            // ── Profile grid ───────────────────────────────────────────────
            val items: List<ProfileRepository.Profile?> = buildList {
                addAll(profiles)
                if (canAddProfile) add(null)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                itemsIndexed(items) { index, profile ->
                    if (profile != null) {
                        ProfileCard(
                            name = profile.name,
                            color = PROFILE_COLORS[index % PROFILE_COLORS.size],
                            onClick = {
                                profileViewModel.selectProfile(profile.id)
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.ProfilePicker.route) { inclusive = false }
                                }
                            },
                        )
                    } else {
                        AddProfileCard(
                            onClick = { navController.navigate(Screen.CreateProfile.route) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(name: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(color)
                .border(3.dp, Color.White.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            // TODO: remplacer par l'image PNG du poisson mascotte via Coil
            Text("🐟", fontSize = 40.sp)
        }
        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AddProfileCard(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .border(2.dp, Color.White.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Ajouter un profil",
                tint = Color.White,
                modifier = Modifier.size(36.dp),
            )
        }
        Text(
            text = "Ajouter",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
        )
    }
}
