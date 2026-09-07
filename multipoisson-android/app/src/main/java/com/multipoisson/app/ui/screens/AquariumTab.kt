package com.multipoisson.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multipoisson.app.ui.theme.AppColors

@Composable
fun AquariumTab(profileId: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.Waves, contentDescription = null, tint = AppColors.Blue, modifier = Modifier.size(64.dp))
            Text("Aquarium", fontSize = 24.sp, fontWeight = FontWeight.Black, color = AppColors.TextPrimary)
            Text("Bientôt disponible", fontSize = 15.sp, color = AppColors.TextSecondary)
        }
    }
}
