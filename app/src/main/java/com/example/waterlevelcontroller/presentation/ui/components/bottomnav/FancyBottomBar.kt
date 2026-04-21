package com.example.waterlevelcontroller.presentation.ui.components.bottomnav

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.waterlevelcontroller.presentation.ui.theme.ActiveBlue
import com.example.waterlevelcontroller.presentation.ui.theme.NavBg
import com.example.waterlevelcontroller.presentation.ui.theme.NavBorder
import com.example.waterlevelcontroller.presentation.ui.theme.TextSecondary


// ─── Colors ───────────────────────────────────────────────────


@Composable
fun FancyBottomBar(navController: NavController) {



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBg)
            .border(border = BorderStroke(0.5.dp, NavBorder), shape = RoundedCornerShape(0.dp))
            .padding(vertical = 10.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround


    ) {



        NavItem(label = "Dashboard", isActive = true, navController = navController, route = "dashboard") {
            Canvas(modifier = Modifier.size(22.dp)) {
                val s = 8.dp.toPx()
                val g = 3.dp.toPx()
                drawRoundRect(
                    color = ActiveBlue,
                    topLeft = Offset(0f, 0f),
                    size = Size(s, s),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
                drawRoundRect(
                    color = ActiveBlue.copy(alpha = 0.3f),
                    topLeft = Offset(s + g, 0f),
                    size = Size(s, s),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
                drawRoundRect(
                    color = ActiveBlue.copy(alpha = 0.3f),
                    topLeft = Offset(0f, s + g),
                    size = Size(s, s),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
                drawRoundRect(
                    color = ActiveBlue.copy(alpha = 0.3f),
                    topLeft = Offset(s + g, s + g),
                    size = Size(s, s),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }
        }
        NavItem(label = "Scheduler", isActive = false, navController = navController, route =  "schedule") {
            Canvas(modifier = Modifier.size(22.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f

                // 1. Draw the clock face outline
                drawCircle(
                    color = TextSecondary,
                    radius = radius,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // 2. Draw the Hour Hand (Shorter)
                // Points from center to 3 o'clock
                drawLine(
                    color = TextSecondary,
                    start = center,
                    end = Offset(center.x + (radius * 0.5f), center.y),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // 3. Draw the Minute Hand (Longer)
                // Points from center up towards 12 o'clock
                drawLine(
                    color = TextSecondary,
                    start = center,
                    end = Offset(center.x, center.y - (radius * 0.7f)),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

    }
}

@Composable
fun NavItem(label: String, navController: NavController, route:String, isActive: Boolean, icon: @Composable () -> Unit) {
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
    ) {
        icon()
        Text(label, fontSize = 10.sp, color = if (isActive) ActiveBlue else TextSecondary)
    }
}