package com.example.waterlevelcontroller.presentation.ui.components.bottomnav

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.waterlevelcontroller.presentation.ui.theme.ActiveBlue
import com.example.waterlevelcontroller.presentation.ui.theme.NavBg
import com.example.waterlevelcontroller.presentation.ui.theme.NavBorder
import com.example.waterlevelcontroller.presentation.ui.theme.TextSecondary


// ─── Colors ───────────────────────────────────────────────────


@Composable
fun FancyBottomBar(navController: NavController) {
    // 1. Observe the current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBg)
            .border(border = BorderStroke(0.5.dp, NavBorder), shape = RoundedCornerShape(0.dp))
            .padding(vertical = 10.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // 2. Logic for Dashboard Item
        val isDashboardActive = currentRoute == "dashboard"
        NavItem(
            label = "Dashboard",
            isActive = isDashboardActive,
            navController = navController,
            route = "dashboard"
        ) {
            DashboardIcon(isActive = isDashboardActive)
        }

        // 3. Logic for Scheduler Item
        val isScheduleActive = currentRoute == "schedule"
        NavItem(
            label = "Scheduler",
            isActive = isScheduleActive,
            navController = navController,
            route = "schedule"
        ) {
            SchedulerIcon(isActive = isScheduleActive)
        }

        val isHistoryActive = currentRoute == "history"
        NavItem(
            label = "History",
            isActive = isHistoryActive,
            navController = navController,
            route = "history"
        ) {
            HistoryIcon(isActive = isHistoryActive)
        }
    }
}

@Composable
fun DashboardIcon(isActive: Boolean) {
    val mainColor = if (isActive) ActiveBlue else TextSecondary
    val secondaryColor =
        if (isActive) ActiveBlue.copy(alpha = 0.3f) else TextSecondary.copy(alpha = 0.3f)

    Canvas(modifier = Modifier.size(22.dp)) {
        val s = 8.dp.toPx()
        val g = 3.dp.toPx()
        drawRoundRect(
            color = mainColor,
            topLeft = Offset(0f, 0f),
            size = Size(s, s),
            cornerRadius = CornerRadius(2.dp.toPx())
        )
        drawRoundRect(
            color = secondaryColor,
            topLeft = Offset(s + g, 0f),
            size = Size(s, s),
            cornerRadius = CornerRadius(2.dp.toPx())
        )
        drawRoundRect(
            color = secondaryColor,
            topLeft = Offset(0f, s + g),
            size = Size(s, s),
            cornerRadius = CornerRadius(2.dp.toPx())
        )
        drawRoundRect(
            color = secondaryColor,
            topLeft = Offset(s + g, s + g),
            size = Size(s, s),
            cornerRadius = CornerRadius(2.dp.toPx())
        )
    }
}

@Composable
fun SchedulerIcon(isActive: Boolean) {
    val color = if (isActive) ActiveBlue else TextSecondary

    Canvas(modifier = Modifier.size(22.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f

        drawCircle(
            color = color,
            radius = radius,
            style = Stroke(width = 1.5.dp.toPx())
        )

        drawLine(
            color = color,
            start = center,
            end = Offset(center.x + (radius * 0.5f), center.y),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = center,
            end = Offset(center.x, center.y - (radius * 0.7f)),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun HistoryIcon(isActive: Boolean) {
    val color = if (isActive) ActiveBlue else TextSecondary

    Canvas(modifier = Modifier.size(22.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f
        val strokeWidth = 1.5.dp.toPx()

        // Outer circle
        drawCircle(
            color = color,
            radius = radius,
            style = Stroke(width = strokeWidth)
        )

        // Clock hand - hour (short)
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x - radius * 0.4f, center.y - radius * 0.4f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Clock hand - minute (long)
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x + radius * 0.5f, center.y - radius * 0.1f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Counter-clockwise arrow at top left of circle
        val arrowRadius = radius * 0.75f
        drawArc(
            color = color,
            startAngle = 160f,
            sweepAngle = -240f,
            useCenter = false,
            topLeft = Offset(center.x - arrowRadius, center.y - arrowRadius),
            size = Size(arrowRadius * 2, arrowRadius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Arrow head at end of arc
        val arrowTipX = center.x - arrowRadius * 0.55f
        val arrowTipY = center.y + arrowRadius * 0.75f
        drawLine(
            color = color,
            start = Offset(arrowTipX, arrowTipY),
            end = Offset(arrowTipX - 4.dp.toPx(), arrowTipY - 1.dp.toPx()),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(arrowTipX, arrowTipY),
            end = Offset(arrowTipX + 1.dp.toPx(), arrowTipY - 4.dp.toPx()),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun NavItem(
    label: String,
    navController: NavController,
    route: String,
    isActive: Boolean,
    icon: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clickable {
                navController.navigate(route) {
                    popUpTo("dashboard")
                    launchSingleTop = true
                }
            }
            .padding(horizontal = 32.dp, vertical = 8.dp)
    ) {
        icon()
        Text(label, fontSize = 10.sp, color = if (isActive) ActiveBlue else TextSecondary)
    }
}