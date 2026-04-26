package com.example.waterlevelcontroller.presentation.ui.components.common

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waterlevelcontroller.presentation.ui.theme.GreenDark
import com.example.waterlevelcontroller.presentation.ui.theme.GreenLight
import com.example.waterlevelcontroller.presentation.ui.theme.TextPrimary
import org.w3c.dom.Text

// ─── Top Bar ──────────────────────────────────────────────────
@Composable
fun TopBar(text: String, isOnline: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Medium)

        if (isOnline) {
            LiveBadge()
        } else {
            OfflineBadge()
        }
    }
}

@Composable
fun LiveBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "live")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulse"
    )
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(GreenLight)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(GreenDark.copy(alpha = alpha))
        )
        Text("Live", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = GreenDark)
    }
}

@Composable
fun OfflineBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "offline")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulse"
    )
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Red.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Color.Red.copy(alpha = alpha))
        )
        Text("Offline", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Red)
    }
}