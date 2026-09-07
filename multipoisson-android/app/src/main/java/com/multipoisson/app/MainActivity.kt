package com.multipoisson.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.multipoisson.app.navigation.AppNavGraph
import com.multipoisson.app.ui.theme.AppColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.Background),
            ) {
                AppNavGraph(navController = navController)
            }
        }
    }
}
