package com.multipoisson.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multipoisson.app.domain.FishId
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.AquariumViewModel

// ── Fish catalog ───────────────────────────────────────────────────────────────

private data class FishSection(val title: String, val ids: List<String>)

private val SECTIONS = listOf(
    FishSection("🐟  Tes tables", FishId.ALL_TABLE),
    FishSection("🐬  Régularité", FishId.ALL_REGULAR),
    FishSection("🦈  Performance", FishId.ALL_PERF),
    FishSection("✨  Spéciaux", listOf(FishId.BOUQUET, FishId.LEGENDARY)),
)

private fun fishColor(fishId: String): Color = when (fishId) {
    FishId.TABLE_1  -> Color(0xFF757575)
    FishId.TABLE_2  -> Color(0xFFE91E8C)
    FishId.TABLE_3  -> Color(0xFF66BB6A)
    FishId.TABLE_4  -> Color(0xFF388E3C)
    FishId.TABLE_5  -> Color(0xFF1565C0)
    FishId.TABLE_6  -> Color(0xFF7B1FA2)
    FishId.TABLE_7  -> Color(0xFFF57C00)
    FishId.TABLE_8  -> Color(0xFFC62828)
    FishId.TABLE_9  -> Color(0xFF6D4C41)
    FishId.TABLE_10 -> Color(0xFFF9A825)
    FishId.REGULAR_7  -> Color(0xFF26C6DA)
    FishId.REGULAR_20 -> Color(0xFF0288D1)
    FishId.REGULAR_50 -> Color(0xFF6A1B9A)
    FishId.PERF_50_80  -> Color(0xFF78909C)
    FishId.PERF_100_90 -> Color(0xFF455A64)
    FishId.PERF_100_95 -> Color(0xFF263238)
    FishId.BOUQUET  -> Color(0xFFFFB300)
    FishId.LEGENDARY -> Color(0xFF6A1B9A)
    else -> AppColors.Blue
}

private fun fishEmoji(fishId: String): String = when {
    fishId.startsWith("table_") -> "🐟"
    fishId.startsWith("regular_") -> "🐬"
    fishId.startsWith("perf_") -> "🦈"
    fishId == FishId.BOUQUET -> "🌟"
    fishId == FishId.LEGENDARY -> "🌈"
    else -> "🐟"
}

private fun unlockHint(fishId: String): String = when (fishId) {
    FishId.TABLE_1  -> "Maîtrise la\ntable de 1"
    FishId.TABLE_2  -> "Maîtrise la\ntable de 2"
    FishId.TABLE_3  -> "Maîtrise la\ntable de 3"
    FishId.TABLE_4  -> "Maîtrise la\ntable de 4"
    FishId.TABLE_5  -> "Maîtrise la\ntable de 5"
    FishId.TABLE_6  -> "Maîtrise la\ntable de 6"
    FishId.TABLE_7  -> "Maîtrise la\ntable de 7"
    FishId.TABLE_8  -> "Maîtrise la\ntable de 8"
    FishId.TABLE_9  -> "Maîtrise la\ntable de 9"
    FishId.TABLE_10 -> "Maîtrise la\ntable de 10"
    FishId.REGULAR_7  -> "Joue\n7 jours"
    FishId.REGULAR_20 -> "Joue\n20 jours"
    FishId.REGULAR_50 -> "Joue\n50 jours"
    FishId.PERF_50_80  -> "50 questions\n80%+ chrono"
    FishId.PERF_100_90 -> "100 questions\n90%+ chrono"
    FishId.PERF_100_95 -> "100 questions\n95%+ chrono"
    FishId.BOUQUET   -> "Maîtrise\ntoutes les tables"
    FishId.LEGENDARY -> "100/100\navec chrono"
    else -> "???"
}

// ── Screen ─────────────────────────────────────────────────────────────────────

@Composable
fun AquariumTab(profileId: String) {
    val vm: AquariumViewModel = viewModel()
    val unlockedIds by vm.unlockedIds.collectAsState()
    val activeMascotId by vm.activeMascotId.collectAsState()

    val totalFish = SECTIONS.sumOf { it.ids.size }
    val unlockedCount = unlockedIds.size.coerceAtMost(totalFish)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            ) {
                Text("🌊 Aquarium", fontSize = 24.sp, fontWeight = FontWeight.Black, color = AppColors.TextPrimary)
                Spacer(Modifier.height(4.dp))
                // Progress bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Collection", fontSize = 12.sp, color = AppColors.TextSecondary)
                        Text("$unlockedCount / $totalFish", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Blue)
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { unlockedCount.toFloat() / totalFish },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = AppColors.Blue,
                        trackColor = AppColors.BlueLight,
                    )
                }
            }
        }

        // ── Sections ──────────────────────────────────────────────────────────
        SECTIONS.forEach { section ->
            // Section header
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    section.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
                )
            }

            // Fish cards
            items(section.ids) { fishId ->
                val unlocked = fishId in unlockedIds
                val isActive = fishId == activeMascotId
                FishCard(
                    fishId = fishId,
                    unlocked = unlocked,
                    isActive = isActive,
                    onClick = { if (unlocked) vm.selectMascot(fishId) },
                )
            }
        }
    }
}

// ── Fish card ──────────────────────────────────────────────────────────────────

@Composable
private fun FishCard(
    fishId: String,
    unlocked: Boolean,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val cardColor = if (unlocked) fishColor(fishId) else Color(0xFFCFD8DC)

    Box(
        modifier = Modifier
            .aspectRatio(0.82f)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (unlocked) Brush.verticalGradient(listOf(cardColor.copy(alpha = 0.85f), cardColor))
                else Brush.verticalGradient(listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC)))
            )
            .then(
                if (isActive) Modifier.border(3.dp, AppColors.Blue, RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .alpha(if (unlocked) 1f else 0.55f),
        ) {
            Text(
                if (unlocked) fishEmoji(fishId) else "🔒",
                fontSize = 34.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (unlocked) FishId.label(fishId) else unlockHint(fishId),
                fontSize = 10.sp,
                fontWeight = if (unlocked) FontWeight.Bold else FontWeight.Normal,
                color = if (unlocked) Color.White else AppColors.TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Active crown badge
        if (isActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
                    .size(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.Blue),
                contentAlignment = Alignment.Center,
            ) {
                Text("👑", fontSize = 11.sp)
            }
        }
    }
}
