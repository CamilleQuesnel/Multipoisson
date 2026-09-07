package com.multipoisson.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.multipoisson.app.ui.theme.AppColors

/** Stub — sera rempli dans le bloc Onboarding (Bloc G). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouveau profil", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background),
            )
        },
        containerColor = AppColors.Background,
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(Icons.Filled.PersonAdd, contentDescription = null, tint = AppColors.Blue, modifier = Modifier.size(64.dp))
                Text("Créer un profil", fontSize = 22.sp, fontWeight = FontWeight.Black, color = AppColors.TextPrimary)
                Text("Écran d'onboarding — Bloc G", fontSize = 14.sp, color = AppColors.TextSecondary)
            }
        }
    }
}
