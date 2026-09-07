package com.multipoisson.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multipoisson.app.data.preferences.AppPreferences
import androidx.compose.ui.platform.LocalContext
import com.multipoisson.app.navigation.Screen
import com.multipoisson.app.ui.theme.AppColors
import com.multipoisson.app.ui.viewmodel.GameViewModel

private enum class Tab(val label: String, val icon: ImageVector) {
    JOUER("Jouer", Icons.Filled.PlayArrow),
    AQUARIUM("Aquarium", Icons.Filled.Waves),
    EVENEMENTS("Événements", Icons.Filled.Celebration),
}

@Composable
fun MainScaffold(navController: NavController) {
    val context = LocalContext.current
    val appPrefs = remember { AppPreferences(context) }
    val activeProfileId by appPrefs.activeProfileId.collectAsState(initial = null)
    val profileId = activeProfileId ?: ""

    // Scoped to Main back-stack entry — same instance the game flow uses
    val gameViewModel: GameViewModel = viewModel()

    var selectedTab by remember { mutableStateOf(Tab.JOUER) }

    Scaffold(
        containerColor = AppColors.Background,
        bottomBar = {
            NavigationBar(
                containerColor = AppColors.White,
                tonalElevation = 0.dp,
            ) {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(tab.icon, contentDescription = tab.label)
                        },
                        label = {
                            Text(
                                tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AppColors.Blue,
                            selectedTextColor = AppColors.Blue,
                            indicatorColor = AppColors.BlueLight,
                            unselectedIconColor = AppColors.TextSecondary,
                            unselectedTextColor = AppColors.TextSecondary,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // ── Tab content ────────────────────────────────────────────────
            when (selectedTab) {
                Tab.JOUER      -> JouerTab(navController = navController, gameViewModel = gameViewModel)
                Tab.AQUARIUM   -> AquariumTab(profileId = profileId)
                Tab.EVENEMENTS -> EvenementsTab(profileId = profileId)
            }

            // ── Gear icon — top-right, always visible on main ──────────────
            IconButton(
                onClick = { navController.navigate(Screen.Parents.route) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .size(44.dp),
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Espace parents",
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}
