package com.example.waterlevelcontroller.presentation.ui.screens.history

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.remote.creation.second
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.waterlevelcontroller.core.utils.Lttb
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.domain.model.PumpLogs
import com.example.waterlevelcontroller.presentation.ui.components.common.LogModeIcon
import java.text.SimpleDateFormat
import java.util.*

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

// ─── UI Models ──────────────────────────────────────────────
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
fun HistoryScreen(
    viewModel: HistoryScreenViewModel = hiltViewModel()
) {
    val resourceState by viewModel.logsState.collectAsState()

    val filters = listOf("Today", "This week", "This month", "All time")
    var selectedFilter by remember { mutableStateOf(3) }
    // 1. Collect Paging Data
    val lazyPagingItems = viewModel.pumpLogFlow.collectAsLazyPagingItems()

    // Transform Firestore Domain data into UI-friendly DayLog groups
    val dayLogs = remember(resourceState) {
        if (resourceState is Resource.Success) {
            transformFirestoreToUi((resourceState as Resource.Success).data ?: emptyList())
        } else emptyList()
    }

    Scaffold(containerColor = ScreenBg) { padding ->
        when (val state = resourceState) {
            is Resource.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ActiveBlue)
                }
            }

            is Resource.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = Color.Red)
                }
            }

            is Resource.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        val fakeData = remember { generateMassiveFakeData() }

                        Text(
                            "YEARLY TREND (LTTB DOWNSAMPLED)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        YearlyRuntimeChart(rawData = fakeData)

                        Spacer(Modifier.height(16.dp))
                    }

                    item {
                        StatsRow()
                        Spacer(Modifier.height(14.dp))
                    }

                    item {
                        BarChartCard(
                            values = listOf(30, 55, 40, 80, 60, 20, 100),
                            labels = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"),
                            todayIndex = 6
                        )
                        Spacer(Modifier.height(14.dp))
                    }

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

                    if (dayLogs.isEmpty()) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No logs found", color = TextSecondary)
                            }
                        }
                    }

                    dayLogs.forEach { dayLog ->
                        item {
                            DayGroupHeader(dayLog)
                            Spacer(Modifier.height(6.dp))
                        }
                        items(dayLog.logs) { log ->
                            LogCard(log)
                            Spacer(Modifier.height(6.dp))
                        }
                        item { Spacer(Modifier.height(12.dp)) }
                    }

                    // --- THE PAGED LIST ---
                    items(
                        count = lazyPagingItems.itemCount,
                        key = lazyPagingItems.itemKey { it.id.ifEmpty { "temp_${UUID.randomUUID()}" } }
                    ) { index ->
                        val item = lazyPagingItems[index]
                        if (item != null) {
                            // Map Domain Model to UI Model for the LogCard
                            val uiLog = remember(item) { mapToUiModel(item) }

                            // Optional: Add a Date Header if day changes
                            val showHeader = if (index == 0) true else {
                                val currentDay = getDayString(item.start_timestamp)
                                val prevDay = getDayString(lazyPagingItems[index - 1]?.start_timestamp ?: 0)
                                currentDay != prevDay
                            }

                            if (showHeader) {
                                Text(
                                    text = getDayString(item.start_timestamp),
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            LogCard(uiLog)
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    // --- LOADING & ERROR STATES ---
                    item {
                        when (val state = lazyPagingItems.loadState.append) {
                            is LoadState.Loading -> {
                                Box(
                                    Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = ActiveBlue
                                    )
                                }
                            }

                            is LoadState.Error -> {
                                Text(
                                    "Error loading more logs",
                                    color = Color.Red,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

/**
 * Transformation logic: Converts raw Domain PumpLogs into grouped UI Models
 */
private fun transformFirestoreToUi(domainLogs: List<PumpLogs>): List<DayLog> {
    val dateSdf = SimpleDateFormat("EEE dd MMM", Locale.getDefault())
    val timeSdf = SimpleDateFormat("HH:mm", Locale.getDefault())

    return domainLogs.groupBy { log ->
        // Changed to .start_timestamp
        dateSdf.format(Date(log.start_timestamp * 1000))
    }.map { (date, logsInDay) ->
        // Changed to .end_timestamp and .start_timestamp
        val totalSecs = logsInDay.sumOf { it.end_timestamp - it.start_timestamp }

        DayLog(
            dayLabel = date,
            totalRuns = logsInDay.size,
            totalDuration = "${totalSecs / 60}m",
            logs = logsInDay.map { item ->
                PumpLog(
                    // Changed to .start_timestamp
                    startTime = timeSdf.format(Date(item.start_timestamp * 1000)),
                    // Changed to .end_timestamp
                    endTime = timeSdf.format(Date(item.end_timestamp * 1000)),
                    // Math using the correct names
                    duration = "${(item.end_timestamp - item.start_timestamp) / 60}m",
                    liters = "~${item.consumption_liters.toInt()} L",
                    mode = when {
                        item.stop_reason.contains("AUTO", true) -> PumpMode.AUTO
                        item.stop_reason.contains("SCHEDULED", true) -> PumpMode.SCHEDULED
                        else -> PumpMode.MANUAL
                    }
                )
            }
        )
    }
}

// ─── UI Components ───────────────────────────────────────────

@Composable
fun HistoryTopBar(filters: List<String>, selectedFilter: Int, onFilterClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Pump History", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
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

@Composable
fun StatsRow() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard("Today runtime", "1h 45m", "3 cycles today", GreenDark, Modifier.weight(1f))
            StatCard("Week total", "8h 20m", "avg 1h 11m/day", TextPrimary, Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard("Longest run", "2h 10m", "Mon 23 Apr", OrangeText, Modifier.weight(1f))
            StatCard("Pump cycles", "18", "this week", TextPrimary, Modifier.weight(1f))
        }
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
        border = BorderStroke(0.5.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label.uppercase(), fontSize = 10.sp, color = TextSecondary, letterSpacing = 0.4.sp)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = valueColor)
            Text(sub, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

@Composable
fun BarChartCard(values: List<Int>, labels: List<String>, todayIndex: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(0.5.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("DAILY RUNTIME (MINUTES)", fontSize = 10.sp, color = TextSecondary)
            Spacer(Modifier.height(12.dp))
            val maxVal = values.maxOrNull()?.toFloat() ?: 1f
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { i, value ->
                    val isToday = i == todayIndex
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(value / maxVal)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (isToday) BlueFill else BlueFillLight)
                        )
                        Text(
                            labels[i],
                            fontSize = 9.sp,
                            color = if (isToday) ActiveBlue else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayGroupHeader(dayLog: DayLog) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(dayLog.dayLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        Text(
            "${dayLog.totalRuns} runs · ${dayLog.totalDuration}",
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}

private data class LogCardStyle(
    val iconBg: Color,
    val iconColor: Color,
    val pillBg: Color,
    val pillText: Color,
    val pillLabel: String
)

@Composable
fun LogCard(log: PumpLog) {
    val style = when (log.mode) {
        PumpMode.AUTO -> LogCardStyle(GreenLight, GreenDark, GreenLight, GreenDark, "Auto")
        PumpMode.MANUAL -> LogCardStyle(BlueBg, BlueText, BlueBg, BlueText, "Manual")
        PumpMode.SCHEDULED -> LogCardStyle(OrangeBg, OrangeText, OrangeBg, OrangeText, "Scheduled")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(0.5.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(style.iconBg), contentAlignment = Alignment.Center
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
                ModePill(style.pillLabel, style.pillBg, style.pillText)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    log.duration,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(log.liters, fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}



@Composable
fun ModePill(label: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}

//fun generateFakeYearlyData(): List<Pair<Double, Double>> {
//    val random = java.util.Random()
//    return (1..365).map { day ->
//        // Most days have 10-40 mins of runtime, some days have spikes up to 200 mins
//        val baseRuntime = if (random.nextFloat() > 0.95) {
//            random.nextInt(150) + 50 // Spike day
//        } else {
//            random.nextInt(30) + 10 // Normal day
//        }
//        Pair(day.toDouble(), baseRuntime.toDouble())
//    }
//}

fun generateMassiveFakeData(): List<Pair<Double, Double>> {
    val random = java.util.Random()
    return (1..15000).map { i ->
        // Base noise + occasional massive spikes
        val base = random.nextInt(20) + 5
        val spike = if (random.nextFloat() > 0.98) random.nextInt(150) else 0
        Pair(i.toDouble(), (base + spike).toDouble())
    }
}

@Composable
fun YearlyRuntimeChart(
    rawData: List<Pair<Double, Double>>,
    modifier: Modifier = Modifier
) {
    // 1. Downsample from 365 to 60 points using LTTB
    val chartData = remember(rawData) { Lttb.calculate(rawData, 25000) }

    val maxRuntime = chartData.maxOfOrNull { it.second }?.toFloat() ?: 100f
    val maxX = 365f

    Card(
        modifier = modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ANNUAL MOTOR RUNTIME (MINS)", fontSize = 10.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                val path = androidx.compose.ui.graphics.Path()

                chartData.forEachIndexed { index, point ->
                    // Map data to canvas coordinates
                    val x = (point.first.toFloat() / maxX) * width
                    val y = height - (point.second.toFloat() / maxRuntime) * height

                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }

                // Draw the line
                drawPath(
                    path = path,
                    color = Color(0xFF007AFF),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Optional: Draw a subtle gradient fill under the line
                val fillPath = androidx.compose.ui.graphics.Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color(0xFF007AFF).copy(alpha = 0.2f), Color.Transparent)
                    )
                )
            }
        }
    }
}

private fun getDayString(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

private fun mapToUiModel(item: PumpLogs): PumpLog {
    val timeSdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val durationMin = (item.end_timestamp - item.start_timestamp) / 60

    return PumpLog(
        startTime = timeSdf.format(Date(item.start_timestamp * 1000)),
        endTime = timeSdf.format(Date(item.end_timestamp * 1000)),
        duration = "${durationMin}m",
        liters = "~${item.consumption_liters.toInt()} L",
        mode = when {
            item.stop_reason.contains("AUTO", true) -> PumpMode.AUTO
            item.stop_reason.contains("SCHEDULED", true) -> PumpMode.SCHEDULED
            else -> PumpMode.MANUAL
        }
    )
}

//@Composable
//fun YearlyRuntimeChartRaw(
//    rawData: List<Pair<Double, Double>>,
//    modifier: Modifier = Modifier
//) {
//    val maxRuntime = rawData.maxOfOrNull { it.second }?.toFloat() ?: 100f
//    val maxX = 365f
//
//    Card(
//        modifier = modifier.fillMaxWidth().height(200.dp),
//        shape = RoundedCornerShape(14.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text("ANNUAL MOTOR RUNTIME (RAW - 365 PTS)", fontSize = 10.sp, color = Color.Gray)
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Canvas(modifier = Modifier.fillMaxSize()) {
//                val width = size.width
//                val height = size.height
//
//                val path = androidx.compose.ui.graphics.Path()
//
//                rawData.forEachIndexed { index, point ->
//                    val x = (point.first.toFloat() / maxX) * width
//                    val y = height - (point.second.toFloat() / maxRuntime) * height
//
//                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
//                }
//
//                drawPath(
//                    path = path,
//                    color = Color(0xFF8E8E93), // Using a neutral gray for the raw comparison
//                    style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
//                )
//            }
//        }
//    }
//}