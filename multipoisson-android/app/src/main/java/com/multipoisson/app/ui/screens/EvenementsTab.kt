package com.multipoisson.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multipoisson.app.ui.theme.AppColors
import java.util.Calendar

// ── Event model ────────────────────────────────────────────────────────────────

private data class GameEvent(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    /** Inclusive start as MMDD (e.g. 901 = Sept 1). 0 = always */
    val startMd: Int,
    /** Inclusive end as MMDD. 0 = always */
    val endMd: Int,
    val gradientStart: Color,
    val gradientEnd: Color,
    val rewardEmoji: String,
    val rewardLabel: String,
)

// ── Event catalog ──────────────────────────────────────────────────────────────

private val ALL_EVENTS = listOf(
    GameEvent(
        id = "nouvel_an",
        title = "Nouvel An",
        emoji = "🎆",
        description = "Bonne année ! Bonne chance pour cette nouvelle année de tables !",
        startMd = 101, endMd = 107,
        gradientStart = Color(0xFF1A237E), gradientEnd = Color(0xFF3949AB),
        rewardEmoji = "🎇", rewardLabel = "Poisson Feu d'artifice",
    ),
    GameEvent(
        id = "rentree",
        title = "Rentrée des classes",
        emoji = "📚",
        description = "C'est la rentrée ! Le moment parfait pour réviser tes tables de multiplication.",
        startMd = 901, endMd = 915,
        gradientStart = Color(0xFF0277BD), gradientEnd = Color(0xFF01579B),
        rewardEmoji = "🎒", rewardLabel = "Poisson Cartable",
    ),
    GameEvent(
        id = "halloween",
        title = "Fête des récoltes",
        emoji = "🎃",
        description = "Les soirées fraîchissent… Les citrouilles aussi font des multiplications !",
        startMd = 1020, endMd = 1031,
        gradientStart = Color(0xFFE65100), gradientEnd = Color(0xFFBF360C),
        rewardEmoji = "🦇", rewardLabel = "Poisson Chauve-souris",
    ),
    GameEvent(
        id = "noel",
        title = "Festival d'Hiver",
        emoji = "❄️",
        description = "Le froid est là, mais les tables se réchauffent ! Gagne un poisson de Noël.",
        startMd = 1201, endMd = 1231,
        gradientStart = Color(0xFF1565C0), gradientEnd = Color(0xFF0D47A1),
        rewardEmoji = "⛄", rewardLabel = "Poisson Bonhomme de neige",
    ),
    GameEvent(
        id = "paques",
        title = "Pâques",
        emoji = "🐰",
        description = "Le lapin de Pâques cache des œufs… et des questions de multiplication !",
        startMd = 401, endMd = 420,
        gradientStart = Color(0xFF558B2F), gradientEnd = Color(0xFF33691E),
        rewardEmoji = "🥚", rewardLabel = "Poisson Œuf de Pâques",
    ),
    GameEvent(
        id = "terre",
        title = "Jour de la Terre",
        emoji = "🌍",
        description = "Prends soin de la planète… et de tes tables ! Un poisson spécial t'attend.",
        startMd = 422, endMd = 422,
        gradientStart = Color(0xFF2E7D32), gradientEnd = Color(0xFF1B5E20),
        rewardEmoji = "🌱", rewardLabel = "Poisson Feuille",
    ),
    GameEvent(
        id = "ete",
        title = "Festival d'Été",
        emoji = "☀️",
        description = "Le soleil brille fort ! Profites-en pour devenir un champion des tables.",
        startMd = 701, endMd = 831,
        gradientStart = Color(0xFFF57F17), gradientEnd = Color(0xFFE65100),
        rewardEmoji = "🏖️", rewardLabel = "Poisson Plage",
    ),
    GameEvent(
        id = "anniversaire",
        title = "Anniversaire !",
        emoji = "🎂",
        description = "Aujourd'hui c'est ton jour ! MultiPoisson te souhaite un excellent anniversaire.",
        startMd = 0, endMd = 0, // handled separately via profile birthdate
        gradientStart = Color(0xFF6A1B9A), gradientEnd = Color(0xFF4A148C),
        rewardEmoji = "🎁", rewardLabel = "Poisson Cadeau",
    ),
    GameEvent(
        id = "zodiac_cheval",
        title = "Année du Cheval",
        emoji = "🐴",
        description = "Fonce comme un cheval ! Cette année, maîtrise toutes tes tables au galop.",
        startMd = 217, endMd = 1231, // Feb 17 2026 - end of year
        gradientStart = Color(0xFF4E342E), gradientEnd = Color(0xFF3E2723),
        rewardEmoji = "🐴", rewardLabel = "Poisson Cheval",
    ),
)

// ── Date helpers ───────────────────────────────────────────────────────────────

private fun todayMd(): Int {
    val cal = Calendar.getInstance()
    return (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH)
}

private fun GameEvent.isActiveToday(today: Int): Boolean {
    if (id == "anniversaire") return false // handled separately
    if (startMd == 0) return false
    return today in startMd..endMd
}

private fun GameEvent.isUpcoming(today: Int): Boolean {
    if (id == "anniversaire") return false
    if (startMd == 0) return false
    return startMd > today
}

private fun GameEvent.monthLabel(): String {
    val m = startMd / 100
    val d = startMd % 100
    val months = listOf("jan", "fév", "mars", "avr", "mai", "juin", "juil", "août", "sept", "oct", "nov", "déc")
    return "$d ${months.getOrElse(m - 1) { "?" }}"
}

// ── Screen ─────────────────────────────────────────────────────────────────────

@Composable
fun EvenementsTab(profileId: String) {
    val today = remember { todayMd() }
    val active   = remember(today) { ALL_EVENTS.filter { it.isActiveToday(today) } }
    val upcoming = remember(today) { ALL_EVENTS.filter { it.isUpcoming(today) } }
    val past     = remember(today) { ALL_EVENTS.filter { !it.isActiveToday(today) && !it.isUpcoming(today) && it.id != "anniversaire" } }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(AppColors.Background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header
        item {
            Text(
                "🎉 Événements",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        // ── Active events ──────────────────────────────────────────────────
        if (active.isNotEmpty()) {
            item {
                Text("En cours", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary)
            }
            items(active) { event ->
                ActiveEventCard(event)
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppColors.White)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🌊", fontSize = 40.sp)
                        Text(
                            "Aucun événement en cours",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary,
                        )
                        Text(
                            "Reviens bientôt pour des événements spéciaux !",
                            fontSize = 13.sp,
                            color = AppColors.TextSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        // ── Upcoming events ────────────────────────────────────────────────
        if (upcoming.isNotEmpty()) {
            item {
                Spacer(Modifier.height(4.dp))
                Text("À venir", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary)
            }
            items(upcoming) { event ->
                UpcomingEventCard(event)
            }
        }

        // ── Past events ────────────────────────────────────────────────────
        if (past.isNotEmpty()) {
            item {
                Spacer(Modifier.height(4.dp))
                Text("Passés", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary)
            }
            items(past) { event ->
                PastEventCard(event)
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

// ── Active event card ──────────────────────────────────────────────────────────

@Composable
private fun ActiveEventCard(event: GameEvent) {
    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(event.gradientStart, event.gradientEnd)))
            .padding(20.dp),
    ) {
        // Active badge
        Surface(
            modifier = Modifier.align(Alignment.TopEnd),
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.25f),
        ) {
            Text(
                "● EN COURS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(event.emoji, fontSize = 52.sp)
            Text(event.title, fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(event.description, fontSize = 14.sp, color = Color.White.copy(alpha = 0.88f), lineHeight = 20.sp)

            Spacer(Modifier.height(4.dp))

            // Reward fish teaser
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.18f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(event.rewardEmoji, fontSize = 28.sp)
                    Column {
                        Text("Poisson à gagner", fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f), fontWeight = FontWeight.SemiBold)
                        Text(event.rewardLabel, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }
    }
}

// ── Upcoming event card ────────────────────────────────────────────────────────

@Composable
private fun UpcomingEventCard(event: GameEvent) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = AppColors.White,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Colored circle with emoji
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(event.gradientStart, event.gradientEnd))),
                contentAlignment = Alignment.Center,
            ) {
                Text(event.emoji, fontSize = 26.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                Text(
                    "Dès le ${event.monthLabel()}  •  ${event.rewardEmoji} ${event.rewardLabel}",
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary,
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AppColors.BlueLight,
            ) {
                Text(
                    "Bientôt",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Blue,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}

// ── Past event card ────────────────────────────────────────────────────────────

@Composable
private fun PastEventCard(event: GameEvent) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = AppColors.White,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFECEFF1)),
                contentAlignment = Alignment.Center,
            ) {
                Text(event.emoji, fontSize = 22.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextSecondary)
                Text("${event.rewardEmoji} ${event.rewardLabel}", fontSize = 12.sp, color = AppColors.TextSecondary.copy(alpha = 0.7f))
            }

            Text("Terminé", fontSize = 11.sp, color = AppColors.TextSecondary)
        }
    }
}
