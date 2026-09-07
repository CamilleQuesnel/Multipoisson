package com.multipoisson.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.GameViewModel

// Color per table: Pair(vibrant top, vibrant bottom)
private val TABLE_COLORS = listOf(
    Color(0xFF42A5F5) to Color(0xFF1565C0), // ×1  bleu ciel
    Color(0xFF66BB6A) to Color(0xFF2E7D32), // ×2  vert
    Color(0xFFFFCA28) to Color(0xFFF57F17), // ×3  jaune-or
    Color(0xFFEF5350) to Color(0xFFB71C1C), // ×4  rouge
    Color(0xFFAB47BC) to Color(0xFF6A1B9A), // ×5  violet
    Color(0xFFFF7043) to Color(0xFFBF360C), // ×6  orange
    Color(0xFF26C6DA) to Color(0xFF006064), // ×7  cyan
    Color(0xFF5C6BC0) to Color(0xFF1A237E), // ×8  indigo
    Color(0xFFEC407A) to Color(0xFF880E4F), // ×9  rose
    Color(0xFF26A69A) to Color(0xFF004D40), // ×10 teal
)

@Composable
fun TableSelectionScreen(
    gameViewModel: GameViewModel,
    onNext: () -> Unit,
) {
    var selected by remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier.padding(top = 52.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Choisis tes tables",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Sélectionne une ou plusieurs tables",
                fontSize = 16.sp,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Quick selects
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("Toutes" to (1..10).toSet(), "Aucune" to emptySet<Int>()).forEach { (label, set) ->
                OutlinedButton(
                    onClick = { selected = set },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Blue),
                ) {
                    Text(label, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(10) { i ->
                val n = i + 1
                val isSelected = n in selected
                TableCell(
                    n = n,
                    isSelected = isSelected,
                    onClick = {
                        selected = if (isSelected) selected - n else selected + n
                    },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                gameViewModel.setSelectedTables(selected.sorted())
                onNext()
            },
            enabled = selected.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Green,
                disabledContainerColor = AppColors.Border,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp, pressedElevation = 2.dp),
        ) {
            Text("Suivant →", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AppColors.White)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TableCell(n: Int, isSelected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed  -> 0.88f
            isSelected -> 1.05f
            else       -> 1f
        },
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "cellScale$n",
    )
    val (colorTop, colorBottom) = TABLE_COLORS[(n - 1).coerceIn(0, TABLE_COLORS.lastIndex)]

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected)
                    Brush.verticalGradient(listOf(colorTop, colorBottom))
                else
                    Brush.verticalGradient(listOf(colorTop.copy(alpha = 0.18f), colorTop.copy(alpha = 0.10f)))
            )
            .border(
                width = 2.dp,
                color = if (isSelected) colorBottom else colorTop.copy(alpha = 0.45f),
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(8.dp),
    ) {
        Text(
            text = "×$n",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isSelected) Color.White else colorBottom,
        )
    }
}
