package com.example.waterlevelcontroller.presentation.ui.screens.schedule

import android.R
import android.app.TimePickerDialog
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Switch
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.waterlevelcontroller.presentation.ui.components.common.TopBar
import com.example.waterlevelcontroller.presentation.ui.theme.CardBg
import com.example.waterlevelcontroller.presentation.ui.theme.ScreenBg
import com.example.waterlevelcontroller.presentation.ui.theme.ToggleGreen
import java.util.Calendar

// -------------------- DATA MODEL --------------------

data class ScheduleUiModel(
    val title: String,
    val startTime: String,
    val endTime: String,
    val days: List<String>,
    val isEnabled: Boolean,
    val duration: String
)


@Composable
fun ScheduleScreen() {

    val schedules = remember {
        mutableStateListOf(
            ScheduleUiModel(
                "Morning pump",
                "06:00",
                "07:00",
                listOf("Mo", "Tu", "We", "Th", "Fr"),
                true,
                "1hr"
            ),
            ScheduleUiModel(
                "Evening pump",
                "18:00",
                "18:30",
                listOf("Sa", "Su"),
                false,
                "30 min"
            )
        )
    }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ScreenBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {


                TopBar("Pump Scheduler")

                Spacer(modifier = Modifier.height(16.dp))

                // Next Schedule Card
                NextScheduleCard()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ACTIVE SCHEDULES",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                schedules.forEachIndexed { index, schedule ->
                    ScheduleCard(
                        schedule = schedule,
                        onToggle = {

                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

    }
}

// -------------------- NEXT CARD --------------------

@Composable
fun NextScheduleCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("⏰")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Next schedule", color = Color.Gray)
                Text("Morning pump", fontWeight = FontWeight.Bold)
            }

            Text(
                text = "2h 14m",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -------------------- SCHEDULE CARD --------------------

@Composable
fun ScheduleCard(
    schedule: ScheduleUiModel,
    onToggle: () -> Unit
) {
    val ison = true
    var showDialog by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(schedule.startTime) }
    var endTime by remember { mutableStateOf(schedule.endTime) }
    var untilFull by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val selectedDays =
                remember { mutableStateListOf<String>().apply { addAll(schedule.days) } }

            Row(verticalAlignment = Alignment.CenterVertically) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(schedule.title, color = Color.Gray)

                    Text(
                        "${schedule.startTime} → ${schedule.endTime}",
                        modifier = Modifier.clickable {
                            showDialog = true
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }


                IOSToggle(isOn = ison, onToggle = onToggle)
            }

            Spacer(modifier = Modifier.height(12.dp))

            DaysRow(
                activeDays = selectedDays,
                onDayClick = { day ->
                    if (selectedDays.contains(day)) {
                        selectedDays.remove(day)
                    } else {
                        selectedDays.add(day)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Duration: ${schedule.duration}",
                color = Color(0xFF1976D2),
                fontSize = 13.sp
            )
        }
    }

    if (showDialog) {

        TimeRangeDialog(
            startTime = startTime,
            endTime = endTime,
            untilFull = untilFull,

            onStartTimeChange = { startTime = it },
            onEndTimeChange = { endTime = it },
            onUntilFullChange = { untilFull = it },

            onDismiss = { showDialog = false },

            onSave = { start, end, isUntilFull ->
                showDialog = false

                // ✅ update your schedule here
                // start = "06:00"
                // end = null if untilFull
            }
        )
    }
}

// -------------------- DAYS ROW (WITH CANVAS) --------------------

@Composable
fun DaysRow(activeDays: List<String>, onDayClick: (String) -> Unit) {

    val allDays = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

    Row {
        allDays.forEach { day ->
            DayCircle(day, day in activeDays, onClick = { onDayClick(day) })
        }
    }
}

@Composable
fun DayCircle(text: String, isActive: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f
    )
    Box(
        modifier = Modifier
            .padding(end = 6.dp)
            .size(36.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        @Composable
        fun DayCircle(text: String, isActive: Boolean) {

            Box(
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(36.dp),
                contentAlignment = Alignment.Center
            ) {

                Canvas(modifier = Modifier.matchParentSize()) {
                    drawCircle(
                        color = if (isActive) Color(0xFF1976D2) else Color.LightGray
                    )
                }

                Text(
                    text = text,
                    color = if (isActive) Color.White else Color.DarkGray,
                    fontSize = 12.sp
                )
            }
        }
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = if (isActive) Color(0xFF1976D2) else Color.LightGray
            )
        }

        Text(
            text = text,
            color = if (isActive) Color.White else Color.DarkGray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun IOSToggle(isOn: Boolean, onToggle: () -> Unit) {
    val thumbOffset by animateFloatAsState(
        targetValue = if (isOn) 1f else 0f,
        animationSpec = tween(200),
        label = "toggle"
    )
    Box(
        modifier = Modifier
            .width(50.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isOn) ToggleGreen else Color(0xFFD1D1D6))
            .clickable { onToggle() },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = (2 + thumbOffset * 22).dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}


@Composable
fun TimeRangeDialog(
    startTime: String,
    endTime: String,
    untilFull: Boolean,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onUntilFullChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSave: (String, String?, Boolean) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Spacer(Modifier.height(12.dp))

                Text(
                    "Set pump schedule",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = Color(0xFF1C1C1E)
                )

                Text(
                    "Pump will run automatically at set times",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E93),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // ✅ Better time section labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // ✅ Colored label pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 12.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "Start",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            WheelTimeGroup(
                                label = "",
                                time = startTime,
                                onTimeChange = onStartTimeChange
                            )
                        }
                    }

                    // ✅ Arrow instead of "to"
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(28.dp))
                        Text(
                            "→",
                            color = Color(0xFF8E8E93),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // ✅ Colored label pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (untilFull) Color(0xFFFFF3E0)
                                    else Color(0xFFE3F2FD)
                                )
                                .padding(horizontal = 12.dp, vertical = 3.dp)
                        ) {
                            Text(
                                if (untilFull) "Auto" else "End",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (untilFull) Color(0xFFE65100) else Color(0xFF1565C0)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        if (!untilFull) {
                            WheelTimeGroup(
                                label = "",
                                time = endTime,
                                onTimeChange = onEndTimeChange
                            )
                        } else {
                            // ✅ Better "until full" placeholder
                            Box(
                                modifier = Modifier
                                    .width(64.dp)
                                    .height(132.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFF3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Until\nfull",
                                        fontSize = 12.sp,
                                        color = Color(0xFFE65100),
                                        fontWeight = FontWeight.Medium,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(color = Color(0xFFE5E5EA), thickness = 0.5.dp)

                Spacer(Modifier.height(12.dp))

                // ✅ Better "until full" toggle row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (untilFull) Color(0xFFFFF3E0) else Color(0xFFF9F9FB)
                        )
                        .clickable { onUntilFullChange(!untilFull) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. The Checkbox at the start
                    Checkbox(
                        checked = untilFull,
                        onCheckedChange = null, // Set to null because the Row's clickable handles it
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFE65100), // Matches your OrangeBg theme
                            uncheckedColor = Color(0xFF8E8E93)
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    // 2. The Text Content
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Run until tank is full",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1C1C1E)
                        )
                        Text(
                            text = "Ignores end time, stops when high sensor triggers",
                            fontSize = 11.sp,
                            color = Color(0xFF8E8E93),
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ✅ Better action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = Color(0xFF8E8E93))
                    }
                    Button(
                        onClick = {
                            onSave(startTime, if (untilFull) null else endTime, untilFull)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun WheelTimeGroup(
    label: String,
    time: String,
    onTimeChange: (String) -> Unit
) {
    val hour = time.split(":")[0].toInt()
    val min = time.split(":")[1].toInt()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            VerticalWheelPicker(
                count = 24,
                currentValue = hour,
                onValueChange = { newH -> onTimeChange("%02d:%02d".format(newH, min)) }
            )
            Text(":", fontWeight = FontWeight.Bold)
            VerticalWheelPicker(
                count = 60,
                currentValue = min,
                onValueChange = { newM -> onTimeChange("%02d:%02d".format(hour, newM)) }
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalWheelPicker(
    count: Int,
    currentValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 44.dp
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = currentValue)

    // Sync the scroll position back to the state
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            onValueChange(listState.firstVisibleItemIndex)
        }
    }

    Box(
        modifier = modifier
            .width(64.dp)
            .height(itemHeight * 3), // Show 3 items
        contentAlignment = Alignment.Center
    ) {
        // Selection Overlay
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight),
            color = Color(0xFFF2F2F7),
            shape = RoundedCornerShape(8.dp)
        ) {}

        LazyColumn(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            item { Spacer(modifier = Modifier.height(itemHeight)) }
            items(count) { index ->
                Box(
                    modifier = Modifier.height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "%02d".format(index),
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp,
                            fontWeight = if (listState.firstVisibleItemIndex == index)
                                FontWeight.Bold else FontWeight.Normal,
                            color = if (listState.firstVisibleItemIndex == index)
                                Color.Black else Color.LightGray
                        )
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(itemHeight)) }
        }
    }
}