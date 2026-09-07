package com.multipoisson.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.multipoisson.app.ui.screens.*
import com.multipoisson.app.ui.viewmodel.GameViewModel

sealed class Screen(val route: String) {
    // ── Entry ──────────────────────────────────────────────────────────────
    object ProfilePicker  : Screen("profile_picker")
    object Main           : Screen("main")
    object CreateProfile  : Screen("create_profile")
    object Parents        : Screen("parents")

    // ── Game flow (all parameterless — data lives in GameViewModel) ────────
    object TableSelection : Screen("table_selection")
    object AdvancedConfig : Screen("advanced_config")
    object Countdown      : Screen("countdown")
    object Game           : Screen("game")
    object Result         : Screen("result")
}

private val slideIn      = slideInHorizontally(tween(280)) { it }
private val slideOut     = slideOutHorizontally(tween(280)) { -it }
private val slideInBack  = slideInHorizontally(tween(280)) { -it }
private val slideOutBack = slideOutHorizontally(tween(280)) { it }

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.ProfilePicker.route,
        enterTransition  = { slideIn },
        exitTransition   = { slideOut },
        popEnterTransition  = { slideInBack },
        popExitTransition   = { slideOutBack },
    ) {

        // ── Entry / profile ────────────────────────────────────────────────
        composable(Screen.ProfilePicker.route) {
            ProfilePickerScreen(navController)
        }

        composable(Screen.Main.route) {
            MainScaffold(navController)
        }

        composable(Screen.CreateProfile.route) {
            CreateProfileScreen(navController)
        }

        composable(
            Screen.Parents.route,
            enterTransition     = { slideInVertically(tween(300)) { it } },
            exitTransition      = { slideOutVertically(tween(300)) { it } },
            popEnterTransition  = { slideInVertically(tween(300)) { it } },
            popExitTransition   = { slideOutVertically(tween(300)) { it } },
        ) {
            ParentsScreen(navController)
        }

        // ── Game flow ──────────────────────────────────────────────────────
        // GameViewModel is scoped to the Main back-stack entry so its state
        // survives the whole game flow and is destroyed when returning to Main.

        composable(Screen.TableSelection.route) { back ->
            val mainEntry = remember(back) { navController.getBackStackEntry(Screen.Main.route) }
            val gameViewModel: GameViewModel = viewModel(mainEntry)
            TableSelectionScreen(
                gameViewModel = gameViewModel,
                onNext = { navController.navigate(Screen.AdvancedConfig.route) },
            )
        }

        composable(Screen.AdvancedConfig.route) { back ->
            val mainEntry = remember(back) { navController.getBackStackEntry(Screen.Main.route) }
            val gameViewModel: GameViewModel = viewModel(mainEntry)
            AdvancedConfigScreen(
                gameViewModel = gameViewModel,
                onStart = { navController.navigate(Screen.Countdown.route) },
            )
        }

        composable(
            Screen.Countdown.route,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition  = { fadeOut(tween(200)) },
        ) {
            CountdownScreen(
                onDone = {
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Countdown.route) { inclusive = true }
                    }
                },
            )
        }

        composable(
            Screen.Game.route,
            enterTransition = { fadeIn(tween(150)) },
            exitTransition  = { fadeOut(tween(150)) },
        ) { back ->
            val mainEntry = remember(back) { navController.getBackStackEntry(Screen.Main.route) }
            val gameViewModel: GameViewModel = viewModel(mainEntry)
            GameScreen(
                gameViewModel = gameViewModel,
                onFinish = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Result.route) { back ->
            val mainEntry = remember(back) { navController.getBackStackEntry(Screen.Main.route) }
            val gameViewModel: GameViewModel = viewModel(mainEntry)
            ResultScreen(
                gameViewModel = gameViewModel,
                onReplay = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = false }
                    }
                },
            )
        }
    }
}
