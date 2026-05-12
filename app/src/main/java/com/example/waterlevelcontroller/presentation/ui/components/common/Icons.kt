package com.example.waterlevelcontroller.presentation.ui.components.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.waterlevelcontroller.presentation.ui.screens.history.PumpMode
import com.example.waterlevelcontroller.presentation.ui.theme.ActiveBlue
import com.example.waterlevelcontroller.presentation.ui.theme.GreenDark
import com.example.waterlevelcontroller.presentation.ui.theme.OrangeText
import com.example.waterlevelcontroller.presentation.ui.theme.TextSecondary

@Composable
fun HumidityIcon() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val cx = size.width / 2f
        val color = ActiveBlue
        val strokeWidth = 1.5.dp.toPx()

        // Droplet path
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx, 1.dp.toPx())
            cubicTo(
                cx + 6.dp.toPx(), 6.dp.toPx(),
                cx + 8.dp.toPx(), 11.dp.toPx(),
                cx + 8.dp.toPx(), 13.dp.toPx()
            )
            cubicTo(
                cx + 8.dp.toPx(), 17.dp.toPx(),
                cx - 8.dp.toPx(), 17.dp.toPx(),
                cx - 8.dp.toPx(), 13.dp.toPx()
            )
            cubicTo(
                cx - 8.dp.toPx(), 11.dp.toPx(),
                cx - 6.dp.toPx(), 6.dp.toPx(),
                cx, 1.dp.toPx()
            )
            close()
        }

        drawPath(path = path, color = color.copy(alpha = 0.2f))
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

// ─── Thermometer Icon ─────────────────────────────────────────
@Composable
fun ThermometerIcon() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val cx = size.width / 2f
        val strokeWidth = 1.5.dp.toPx()
        val color = OrangeText

        // Bulb at bottom
        drawCircle(
            color = color,
            radius = 4.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(cx, size.height - 4.dp.toPx())
        )

        // Tube
        drawRoundRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(cx - 2.dp.toPx(), 0f),
            size = androidx.compose.ui.geometry.Size(4.dp.toPx(), size.height - 4.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )

        // Fill inside tube
        drawRoundRect(
            color = color.copy(alpha = 0.5f),
            topLeft = androidx.compose.ui.geometry.Offset(cx - 1.dp.toPx(), size.height * 0.4f),
            size = androidx.compose.ui.geometry.Size(2.dp.toPx(), size.height * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx())
        )
    }
}


@Composable
fun PumpIcon(isOn: Boolean) {
    val color = if (isOn) GreenDark else TextSecondary
    Canvas(modifier = Modifier.size(22.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val r = 7.dp.toPx()
        drawCircle(color = color, radius = r, style = Stroke(width = 1.5.dp.toPx()))
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(cx, cy - r * 0.6f),
            end = androidx.compose.ui.geometry.Offset(cx, cy),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(cx, cy),
            end = androidx.compose.ui.geometry.Offset(cx + r * 0.5f, cy + r * 0.3f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun LogModeIcon(mode: PumpMode, color: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val r = 6.dp.toPx()
        drawCircle(color = color, radius = r, style = Stroke(width = 1.5.dp.toPx()))
        if (mode == PumpMode.AUTO) {
            drawLine(
                color,
                androidx.compose.ui.geometry.Offset(cx - r * 0.5f, cy),
                androidx.compose.ui.geometry.Offset(cx - r * 0.1f, cy + r * 0.4f),
                1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color,
                androidx.compose.ui.geometry.Offset(cx - r * 0.1f, cy + r * 0.4f),
                androidx.compose.ui.geometry.Offset(cx + r * 0.5f, cy - r * 0.4f),
                1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        } else {
            drawLine(
                color,
                androidx.compose.ui.geometry.Offset(cx, cy - r * 0.6f),
                androidx.compose.ui.geometry.Offset(cx, cy),
                1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color,
                androidx.compose.ui.geometry.Offset(cx, cy),
                androidx.compose.ui.geometry.Offset(cx + r * 0.5f, cy + r * 0.3f),
                1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
