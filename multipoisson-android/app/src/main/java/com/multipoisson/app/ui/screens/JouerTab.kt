package com.multipoisson.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.multipoisson.app.navigation.Screen
import com.multipoisson.app.ui.theme.AppColors

/**
 * Placeholder — sera remplacé par l'écran de choix du mode (Bloc C).
 */
@Composable
fun JouerTab(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text("🐟", fontSize = 72.sp)
            Text(
                "MultiPoisson",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = AppColors.Blue,
            )
            Text(
                "Tables de multiplication",
                fontSize = 15.sp,
                color = AppColors.TextSecondary,
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    navController.navigate(Screen.TableSelection.route)
                },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Green),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = AppColors.White)
                Spacer(Modifier.width(8.dp))
                Text("Jouer !", fontSize = 20.sp, fontWeight = FontWeight.Black, color = AppColors.White)
            }
        }
    }
}
