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
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.GameViewModel

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
            isPressed   -> 0.88f
            isSelected  -> 1.05f
            else        -> 1f
        },
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "cellScale$n",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected)
                    Brush.verticalGradient(listOf(AppColors.Blue, AppColors.BlueDark))
                else
                    Brush.verticalGradient(listOf(AppColors.White, AppColors.White))
            )
            .border(
                width = 2.dp,
                color = if (isSelected) AppColors.BlueDark else AppColors.Border,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(8.dp),
    ) {
        Text(
            text = "×$n",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isSelected) AppColors.White else AppColors.TextPrimary,
        )
    }
}
