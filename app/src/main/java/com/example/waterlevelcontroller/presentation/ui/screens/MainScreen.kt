package com.example.waterlevelcontroller

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.waterlevelcontroller.presentation.ui.components.bottomnav.FancyBottomBar
import com.example.waterlevelcontroller.presentation.ui.screens.dashboard.DashboardScreen
import com.example.waterlevelcontroller.presentation.ui.screens.schedule.ScheduleScreen

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            FancyBottomBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(padding)
    ) {

            composable("dashboard") {
                DashboardScreen(navController = navController)
            }

            composable("schedule") {
                ScheduleScreen()
            }
        }
    }
}