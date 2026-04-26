package com.example.waterlevelcontroller.presentation.ui.screens.history


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colors ───────────────────────────────────────────────────
private val ScreenBg = Color(0xFFF5F5F7)
private val CardBg = Color.White
private val CardBorder = Color(0xFFE5E5EA)
private val TextPrimary = Color(0xFF1C1C1E)
private val TextSecondary = Color(0xFF8E8E93)
private val GreenDark = Color(0xFF2E7D32)
private val GreenLight = Color(0xFFE8F5E9)
private val OrangeText = Color(0xFFE65100)
private val OrangeBg = Color(0xFFFFF3E0)
private val BlueText = Color(0xFF1565C0)
private val BlueBg = Color(0xFFE3F2FD)
private val BlueFill = Color(0xFF378ADD)
private val BlueFillLight = Color(0xFFB5D4F4)
private val ActiveBlue = Color(0xFF007AFF)

// ─── Data Models ──────────────────────────────────────────────
enum class PumpMode { AUTO, MANUAL, SCHEDULED }

data class PumpLog(
    val startTime: String,
    val endTime: String,
    val duration: String,
    val liters: String,
    val mode: PumpMode
)

data class DayLog(
    val dayLabel: String,
    val totalRuns: Int,
    val totalDuration: String,
    val logs: List<PumpLog>
)

// ─── Main Screen ──────────────────────────────────────────────
@Composable
fun HistoryScreen() {

    val filters = listOf("Today", "This week", "This month", "All time")
    var selectedFilter by remember { mutableStateOf(1) }

    // Sample data — replace with Firebase data
    val weekData = listOf(30, 55, 40, 80, 60, 20, 100)
    val weekDays = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    val todayIndex = 6

    val dayLogs = listOf(
        DayLog(
            dayLabel = "Today — Sun 23 Apr",
            totalRuns = 3,
            totalDuration = "1h 45m",
            logs = listOf(
                PumpLog("06:00", "07:00", "1h 00m", "~600 L", PumpMode.SCHEDULED),
                PumpLog("11:30", "12:00", "30m", "~300 L", PumpMode.MANUAL),
                PumpLog("15:15", "15:30", "15m", "~150 L", PumpMode.AUTO)
            )
        ),
        DayLog(
            dayLabel = "Sat 22 Apr",
            totalRuns = 2,
            totalDuration = "45m",
            logs = listOf(
                PumpLog("06:00", "06:30", "30m", "~300 L", PumpMode.SCHEDULED),
                PumpLog("18:45", "19:00", "15m", "~150 L", PumpMode.AUTO)
            )
        ),
        DayLog(
            dayLabel = "Fri 21 Apr",
            totalRuns = 3,
            totalDuration = "2h 10m",
            logs = listOf(
                PumpLog("06:00", "07:00", "1h 00m", "~600 L", PumpMode.SCHEDULED),
                PumpLog("12:00", "12:40", "40m", "~400 L", PumpMode.MANUAL),
                PumpLog("20:00", "20:30", "30m", "~300 L", PumpMode.AUTO)
            )
        )
    )

    Scaffold(containerColor = ScreenBg) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Top Bar
            item {
                HistoryTopBar(
                    filters = filters,
                    selectedFilter = selectedFilter,
                    onFilterClick = { selectedFilter = (selectedFilter + 1) % filters.size }
                )
                Spacer(Modifier.height(16.dp))
            }

            // Stats Row
            item {
                StatsRow()
                Spacer(Modifier.height(14.dp))
            }

            // Bar Chart
            item {
                BarChartCard(
                    values = weekData,
                    labels = weekDays,
                    todayIndex = todayIndex
                )
                Spacer(Modifier.height(14.dp))
            }

            // Section Label
            item {
                Text(
                    "LOG",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    letterSpacing = 0.6.sp
                )
                Spacer(Modifier.height(8.dp))
            }

            // Day Groups
            dayLogs.forEach { dayLog ->
                item {
                    DayGroupHeader(dayLog)
                    Spacer(Modifier.height(6.dp))
                }
                items(dayLog.logs) { log ->
                    LogCard(log)
                    Spacer(Modifier.height(6.dp))
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────
@Composable
fun HistoryTopBar(
    filters: List<String>,
    selectedFilter: Int,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Pump History",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(CardBg)
                .border(0.5.dp, CardBorder, RoundedCornerShape(20.dp))
                .clickable { onFilterClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    val strokePx = 1.5.dp.toPx()
                    listOf(
                        Pair(2.dp.toPx(), size.width - 2.dp.toPx()),
                        Pair(4.dp.toPx(), size.width - 4.dp.toPx()),
                        Pair(6.dp.toPx(), size.width - 6.dp.toPx())
                    ).forEachIndexed { i, (start, end) ->
                        drawLine(
                            color = ActiveBlue,
                            start = androidx.compose.ui.geometry.Offset(
                                start,
                                (i * 4 + 2).dp.toPx()
                            ),
                            end = androidx.compose.ui.geometry.Offset(end, (i * 4 + 2).dp.toPx()),
                            strokeWidth = strokePx,
                            cap = StrokeCap.Round
                        )
                    }
                }
                Text(
                    filters[selectedFilter],
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ActiveBlue
                )
            }
        }
    }
}

// ─── Stats Row ────────────────────────────────────────────────
@Composable
fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            label = "Today runtime",
            value = "1h 45m",
            sub = "3 cycles today",
            valueColor = GreenDark,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Week total",
            value = "8h 20m",
            sub = "avg 1h 11m/day",
            valueColor = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            label = "Longest run",
            value = "2h 10m",
            sub = "Mon 23 Apr",
            valueColor = OrangeText,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Pump cycles",
            value = "18",
            sub = "this week",
            valueColor = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    sub: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(0.5.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                label.uppercase(),
                fontSize = 10.sp,
                color = TextSecondary,
                letterSpacing = 0.4.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = valueColor)
            Spacer(Modifier.height(2.dp))
            Text(sub, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

// ─── Bar Chart ────────────────────────────────────────────────
@Composable
fun BarChartCard(values: List<Int>, labels: List<String>, todayIndex: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(0.5.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                "DAILY RUNTIME (MINUTES)",
                fontSize = 10.sp,
                color = TextSecondary,
                letterSpacing = 0.4.sp
            )
            Spacer(Modifier.height(12.dp))

            val maxVal = values.max().toFloat()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { i, value ->
                    val fraction = value / maxVal
                    val isToday = i == todayIndex

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (isToday) BlueFill else BlueFillLight)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            labels[i],
                            fontSize = 9.sp,
                            color = if (isToday) ActiveBlue else TextSecondary,
                            fontWeight = if (isToday) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// ─── Day Group ────────────────────────────────────────────────
@Composable
fun DayGroupHeader(dayLog: DayLog) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(dayLog.dayLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        Text(
            "${dayLog.totalRuns} runs · ${dayLog.totalDuration}",
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}

// ─── Add this data class ───────────────────────────────────────
private data class LogCardStyle(
    val iconBg: Color,
    val iconColor: Color,
    val pillBg: Color,
    val pillText: Color,
    val pillLabel: String
)


// ─── Log Card ─────────────────────────────────────────────────
@Composable
fun LogCard(log: PumpLog) {
    val style = when (log.mode) {
        PumpMode.AUTO -> LogCardStyle(
            iconBg = GreenLight,
            iconColor = GreenDark,
            pillBg = GreenLight,
            pillText = GreenDark,
            pillLabel = "Auto"
        )
        PumpMode.MANUAL -> LogCardStyle(
            iconBg = BlueBg,
            iconColor = BlueText,
            pillBg = BlueBg,
            pillText = BlueText,
            pillLabel = "Manual"
        )
        PumpMode.SCHEDULED -> LogCardStyle(
            iconBg = OrangeBg,
            iconColor = OrangeText,
            pillBg = OrangeBg,
            pillText = OrangeText,
            pillLabel = "Scheduled"
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(0.5.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp, 12.dp, 14.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(style.iconBg),
                contentAlignment = Alignment.Center
            ) {
                LogModeIcon(log.mode, style.iconColor)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${log.startTime} – ${log.endTime}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(3.dp))
                ModePill(
                    label = style.pillLabel,
                    bg = style.pillBg,
                    textColor = style.pillText
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(log.duration, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                Text(log.liters, fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun LogModeIcon(mode: PumpMode, color: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val r = 6.dp.toPx()
        drawCircle(color = color, radius = r, style = Stroke(width = 1.5.dp.toPx()))
        when (mode) {
            PumpMode.AUTO -> {
                // Checkmark
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
            }

            else -> {
                // Clock hands
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
}

@Composable
fun ModePill(label: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}