package com.multipoisson.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import com.multipoisson.app.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multipoisson.app.data.repository.ProfileRepository
import com.multipoisson.app.navigation.Screen
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.ProfileViewModel
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Colors cycling across profile slots (index 0–4). */
private val PROFILE_COLORS = listOf(
    AppColors.Blue,
    AppColors.Green,
    AppColors.Orange,
    AppColors.Purple,
    Color(0xFFE91E8C), // rose vif
)

// ── Bubble configuration ───────────────────────────────────────────────────

private data class BubbleConfig(val xFraction: Float, val size: Dp, val durationMs: Int, val delayMs: Int)

private val BUBBLE_CONFIGS = listOf(
    BubbleConfig(0.18f, 12.dp, 4800,    0),
    BubbleConfig(0.55f,  8.dp, 6200, 1200),
    BubbleConfig(0.80f, 16.dp, 5400, 2400),
    BubbleConfig(0.35f, 10.dp, 7100,  600),
)

@Composable
fun ProfilePickerScreen(navController: NavController) {
    val profileViewModel: ProfileViewModel = viewModel()
    val profiles by profileViewModel.profiles.collectAsState()
    val canAddProfile = profiles.size < 5

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
        // ── Floating bubbles (tap to pop!) ─────────────────────────────────
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            BUBBLE_CONFIGS.forEach { cfg ->
                FloatingBubble(cfg, maxWidth, maxHeight)
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

            // Mascotte flottante
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
            Image(
                painter = painterResource(R.drawable.mascot_base),
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .offset(y = mascotY.dp),
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

// ── Floating bubble (tappable, pops on tap) ────────────────────────────────

@Composable
private fun FloatingBubble(cfg: BubbleConfig, screenW: Dp, screenH: Dp) {
    val trans = rememberInfiniteTransition(label = "b${cfg.durationMs}")
    val yFraction by trans.animateFloat(
        initialValue = 1f,
        targetValue = -0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(cfg.durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(cfg.delayMs),
        ),
        label = "bY${cfg.durationMs}",
    )

    var isPopping by remember { mutableStateOf(false) }
    val popScale  = remember { Animatable(1f) }
    val popAlpha  = remember { Animatable(1f) }
    val particles = remember { Animatable(0f) }

    LaunchedEffect(isPopping) {
        if (!isPopping) return@LaunchedEffect
        launch { popScale.animateTo(2.0f, tween(170, easing = FastOutSlowInEasing)) }
        launch { popAlpha.animateTo(0f,   tween(170)) }
        launch { particles.animateTo(1f,  tween(220)) }
        delay(380)
        popScale.snapTo(1f)
        popAlpha.snapTo(1f)
        particles.snapTo(0f)
        isPopping = false
    }

    Box(
        modifier = Modifier.offset(
            x = screenW * cfg.xFraction,
            y = screenH + screenH * yFraction,
        ),
        contentAlignment = Alignment.Center,
    ) {
        // Burst: 6 dots radiating outward on pop
        if (particles.value > 0f) {
            val pv     = particles.value
            val pAlpha = (1f - pv).coerceIn(0f, 1f)
            Canvas(modifier = Modifier.size(cfg.size * 5f)) {
                val cx      = size.width / 2f
                val cy      = size.height / 2f
                val maxDist = cfg.size.toPx() * 2.2f
                listOf(0f, 60f, 120f, 180f, 240f, 300f).forEach { angle ->
                    val rad  = Math.toRadians(angle.toDouble())
                    val dist = maxDist * pv
                    drawCircle(
                        color  = Color.White.copy(alpha = pAlpha * 0.9f),
                        radius = 3.dp.toPx(),
                        center = Offset(
                            cx + (dist * cos(rad)).toFloat(),
                            cy + (dist * sin(rad)).toFloat(),
                        ),
                    )
                }
            }
        }

        // Bubble itself
        Box(
            modifier = Modifier
                .size(cfg.size)
                .scale(popScale.value)
                .alpha(popAlpha.value)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f))
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !isPopping,
                ) { isPopping = true },
        )
    }
}

// ── Profile cards ──────────────────────────────────────────────────────────

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
