package com.example.waterlevelcontroller.presentation.ui.screens.schedule

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.domain.model.Schedule
import com.example.waterlevelcontroller.domain.model.ScheduleSettings
import com.example.waterlevelcontroller.domain.model.SyncStatus
import com.example.waterlevelcontroller.domain.model.TimeWindow
import com.example.waterlevelcontroller.presentation.ui.components.common.TopBar
import com.example.waterlevelcontroller.presentation.ui.theme.*

// -------------------- DATA MODEL --------------------

// -------------------- MAIN SCREEN --------------------

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    // 1. Observe state from ViewModel using lifecycle-aware collection
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val scheduleResource by viewModel.ScheduleState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // 2. Start observing Firebase data on launch
    LaunchedEffect(Unit) {
        viewModel.observeSchedule()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ScreenBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- FIXED HEADER SECTION ---
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                TopBar("Pump Scheduler", isOnline)
                Spacer(modifier = Modifier.height(16.dp))
                NextScheduleCard()
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE SCHEDULES",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    IconButton(
                        onClick = {
                            // 3. Add to Firebase via ViewModel
                            viewModel.addSchedule(createDefaultSchedule())
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Add, "Add", tint = ActiveBlue)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- DATA STATE HANDLING ---
            // Handles Loading, Success, and Error states from the Repository
            when (val resource = scheduleResource) {
                is Resource.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ActiveBlue)
                    }
                }

                is Resource.Success -> {
                    val list = resource.data ?: emptyList()
                    if (list.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No schedules found.", color = TextSecondary)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Use ID as key for efficient list updates
                            items(items = list, key = { it.id }) { schedule ->
                                ScheduleCard(
                                    schedule = schedule,
                                    onToggle = {
                                        // 4. Update Firebase via ViewModel
                                        viewModel.updateSchedule(
                                            schedule.copy(
                                                settings = schedule.settings.copy(
                                                    isEnabled = !schedule.settings.isEnabled
                                                )
                                            )
                                        )
                                    },
                                    // ADD THIS LOGIC:
                                    onDayChanged = { updatedDays ->
                                        viewModel.updateSchedule(
                                            schedule.copy(activeDays = updatedDays)
                                        )
                                    },
                                    onDelete = {
                                        viewModel.deleteSchedule(schedule.id)
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(30.dp)) }
                        }
                    }
                }

                is Resource.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${resource.message}", color = Color.Red)
                    }
                }
            }
        }
    }
}
// -------------------- COMPONENTS --------------------

@Composable
fun ScheduleCard(
    schedule: Schedule,
    onToggle: () -> Unit,
    onDayChanged: (List<Int>) -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(schedule.timeWindow.start) }
    var endTime by remember { mutableStateOf(schedule.timeWindow.end ?: "09:00") }
    val untilFull = schedule.settings.untilFull

    val dayMap = mapOf("Mo" to 1, "Tu" to 2, "We" to 3, "Th" to 4, "Fr" to 5, "Sa" to 6, "Su" to 7)
    val revMap = dayMap.entries.associate { it.value to it.key }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(0.5.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(schedule.title, color = TextSecondary, fontSize = 12.sp)
                    Text(
                        text = if (untilFull) "$startTime → Full" else "$startTime → $endTime",
                        modifier = Modifier.clickable { showDialog = true },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                IOSToggle(isOn = schedule.settings.isEnabled, onToggle = onToggle)
            }

            Spacer(modifier = Modifier.height(12.dp))

            DaysRow(
                activeDays = schedule.activeDays.map { revMap[it] ?: "" },
                onDayClick = { dayName ->
                    val dayId = dayMap[dayName] ?: return@DaysRow
                    val newDays = if (schedule.activeDays.contains(dayId)) {
                        schedule.activeDays.filter { it != dayId }
                    } else {
                        (schedule.activeDays + dayId).sorted()
                    }
                    onDayChanged(newDays) // Sends the update to ViewModel
                }
            )

// BOTTOM ROW: SYNC STATUS & DELETE ACTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sync Status Indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (schedule.syncStatus == SyncStatus.Synced) GreenDark
                                else OrangeText
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (schedule.syncStatus == SyncStatus.Synced) "Synced" else "Pending Sync",
                        color = if (schedule.syncStatus == SyncStatus.Synced) GreenDark else OrangeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Delete Icon Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Schedule",
                        tint = Color.Red.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    if (showDialog) {
        TimeRangeDialog(
            startTime = startTime,
            endTime = endTime,
            untilFull = untilFull,
            onStartTimeChange = { startTime = it },
            onEndTimeChange = { endTime = it },
            onUntilFullChange = { /* Update via VM */ },
            onDismiss = { showDialog = false },
            onSave = { _, _, _ -> showDialog = false }
        )
    }
}
// -------------------- THE REST OF YOUR COMPONENTS (DayCircle, IOSToggle, etc.) --------------------
// (Keep the rest of the code from the previous response for NextScheduleCard, DaysRow, DayCircle, IOSToggle, TimeRangeDialog, WheelTimeGroup, and VerticalWheelPicker)

@Composable
fun NextScheduleCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(0.5.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(GreenLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("⏰")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Next schedule", color = TextSecondary, fontSize = 12.sp)
                Text("Morning pump", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Text(text = "2h 14m", color = GreenDark, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DaysRow(activeDays: List<String>, onDayClick: (String) -> Unit) {
    val allDays = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        allDays.forEach { day ->
            DayCircle(day, day in activeDays, onClick = { onDayClick(day) })
        }
    }
}

@Composable
fun DayCircle(text: String, isActive: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(targetValue = if (isActive) 1.1f else 1f)
    Box(
        modifier = Modifier
            .size(36.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(color = if (isActive) ActiveBlue else PillGray)
        }
        Text(
            text = text,
            color = if (isActive) Color.White else TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun IOSToggle(isOn: Boolean, onToggle: () -> Unit) {
    val thumbOffset by animateFloatAsState(
        targetValue = if (isOn) 1f else 0f,
        animationSpec = tween(200)
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
                Text(
                    "Set pump schedule",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    "Pump will run automatically at set times",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimePickerColumn(
                        "Start",
                        startTime,
                        onStartTimeChange,
                        Color(0xFFE8F5E9),
                        Color(0xFF2E7D32)
                    )
                    Text("→", color = TextSecondary, fontSize = 18.sp)
                    if (!untilFull) {
                        TimePickerColumn(
                            "End",
                            endTime,
                            onEndTimeChange,
                            Color(0xFFE3F2FD),
                            Color(0xFF1565C0)
                        )
                    } else {
                        UntilFullPlaceholder()
                    }
                }

                Spacer(Modifier.height(24.dp))
                UntilFullToggle(untilFull, onToggle = onUntilFullChange)
                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Cancel", color = TextSecondary) }
                    Button(
                        onClick = {
                            onSave(
                                startTime,
                                if (untilFull) null else endTime,
                                untilFull
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ActiveBlue)
                    ) { Text("Save") }
                }
            }
        }
    }
}

@Composable
fun TimePickerColumn(
    label: String,
    time: String,
    onTimeChange: (String) -> Unit,
    bg: Color,
    txt: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(bg)
                .padding(horizontal = 12.dp, vertical = 3.dp)
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = txt)
        }
        Spacer(Modifier.height(8.dp))
        WheelTimeGroup(time, onTimeChange)
    }
}

@Composable
fun UntilFullPlaceholder() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(OrangeBg)
                .padding(horizontal = 12.dp, vertical = 3.dp)
        ) {
            Text("Auto", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrangeText)
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(64.dp, 132.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(OrangeBg), contentAlignment = Alignment.Center
        ) {
            Text(
                "Until\nfull",
                fontSize = 12.sp,
                color = OrangeText,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun UntilFullToggle(untilFull: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (untilFull) OrangeBg else Color(0xFFF9F9FB))
            .clickable { onToggle(!untilFull) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = untilFull,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(checkedColor = OrangeText)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                "Run until tank is full",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text("Stops when high sensor triggers", fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
fun WheelTimeGroup(time: String, onTimeChange: (String) -> Unit) {
    val hour = time.split(":")[0].toIntOrNull() ?: 0
    val min = time.split(":")[1].toIntOrNull() ?: 0
    Row(verticalAlignment = Alignment.CenterVertically) {
        VerticalWheelPicker(24, hour) { onTimeChange("%02d:%02d".format(it, min)) }
        Text(":", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 2.dp))
        VerticalWheelPicker(60, min) { onTimeChange("%02d:%02d".format(hour, it)) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalWheelPicker(count: Int, currentValue: Int, onValueChange: (Int) -> Unit) {
    val itemHeight = 44.dp
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = currentValue)
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) onValueChange(
            listState.firstVisibleItemIndex
        )
    }

    Box(modifier = Modifier.size(50.dp, itemHeight * 3), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight),
            color = Color(0xFFF2F2F7),
            shape = RoundedCornerShape(8.dp)
        ) {}
        LazyColumn(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(Modifier.height(itemHeight)) }
            items(count) { index ->
                Box(Modifier.height(itemHeight), contentAlignment = Alignment.Center) {
                    Text(
                        "%02d".format(index),
                        fontSize = 18.sp,
                        fontWeight = if (listState.firstVisibleItemIndex == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (listState.firstVisibleItemIndex == index) Color.Black else Color.LightGray
                    )
                }
            }
            item { Spacer(Modifier.height(itemHeight)) }
        }
    }
}


// -------------------- HELPERS --------------------

fun Int.toDayName(): String = when (this) {
    7 -> "Su"
    1 -> "Mo"
    2 -> "Tu"
    3 -> "We"
    4 -> "Th"
    5 -> "Fr"
    6 -> "Sa"
    else -> ""
}

private fun createDefaultSchedule() = Schedule(
    id = "",
    title = "New Schedule",
    timeWindow = TimeWindow(start = "08:00", end = "09:30"),
    activeDays = listOf(1, 2, 3, 4, 5),
    settings = ScheduleSettings(isEnabled = true, untilFull = false),
    syncStatus = SyncStatus.Pending,
    createdBy = "user_123",
    lastEditedBy = "user_123",
    lastEditedName = "Yogesh",
    lastUpdated = System.currentTimeMillis()
)