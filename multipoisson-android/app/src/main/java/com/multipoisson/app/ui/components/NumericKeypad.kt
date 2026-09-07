package com.multipoisson.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multipoisson.app.ui.theme.AppColors

private val ROWS = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf("←", "0", "✓"),
)

@Composable
fun NumericKeypad(
    value: String,
    onChange: (String) -> Unit,
    onValidate: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ROWS.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { label ->
                    KeypadButton(
                        label = label,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when {
                                label == "✓"    -> onValidate()
                                label == "←"    -> onChange(value.dropLast(1))
                                value.length < 4 -> onChange(value + label)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    val isValidate = label == "✓"
    val isDelete   = label == "←"
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.91f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 600f),
        label = "keyScale",
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(62.dp)
            .scale(scale),
        shape = RoundedCornerShape(14.dp),
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isValidate -> AppColors.Green
                isDelete   -> AppColors.Orange
                else       -> AppColors.White
            },
            contentColor = when {
                isValidate || isDelete -> Color.White
                else -> AppColors.TextPrimary
            },
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isValidate || isDelete) 5.dp else 3.dp,
            pressedElevation = 1.dp,
        ),
    ) {
        Text(
            text = label,
            fontSize = if (isValidate || isDelete) 22.sp else 24.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
