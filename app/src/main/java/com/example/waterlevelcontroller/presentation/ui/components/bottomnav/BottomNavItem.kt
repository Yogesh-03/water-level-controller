package com.example.waterlevelcontroller.presentation.ui.components.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem(
        "dashboard",
        "Dashboard",
        Icons.Default.Home
    )

    object Schedule : BottomNavItem(
        "schedule",
        "Schedule",
        Icons.Default.DateRange
    )
}