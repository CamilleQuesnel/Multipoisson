package com.multipoisson.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multipoisson.app.data.preferences.AppPreferences
import com.multipoisson.app.data.repository.ProfileRepository
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
    val profileRepo = remember { ProfileRepository(context) }
    val activeProfileId by appPrefs.activeProfileId.collectAsState(initial = null)
    val profileId = activeProfileId ?: ""

    val allProfiles by profileRepo.observeAll().collectAsState(initial = emptyList())
    val currentProfile = allProfiles.find { it.id == profileId }

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

            // ── Avatar chip — top-left, taps to switch player ─────────────
            if (currentProfile != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 8.dp, start = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppColors.White)
                        .border(1.5.dp, AppColors.Border, RoundedCornerShape(20.dp))
                        .clickable { navController.navigate(Screen.ProfilePicker.route) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("🐟", fontSize = 16.sp)
                    Text(
                        currentProfile.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
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
