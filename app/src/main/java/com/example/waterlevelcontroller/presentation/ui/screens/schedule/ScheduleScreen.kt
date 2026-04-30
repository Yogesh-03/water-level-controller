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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.waterlevelcontroller.domain.model.Schedule
import com.example.waterlevelcontroller.domain.model.ScheduleSettings
import com.example.waterlevelcontroller.domain.model.SyncStatus
import com.example.waterlevelcontroller.domain.model.TimeWindow
import com.example.waterlevelcontroller.presentation.ui.components.common.TopBar
import com.example.waterlevelcontroller.presentation.ui.theme.*

// -------------------- DATA MODEL --------------------

data class ScheduleUiModel(
    val id: Long = System.currentTimeMillis(), // Added ID to uniquely identify for deletion
    val title: String,
    val startTime: String,
    val endTime: String,
    val days: List<String>,
    val isEnabled: Boolean,
    val duration: String
)

// -------------------- MAIN SCREEN --------------------

@Composable
fun ScheduleScreen() {
    val schedules = remember {
        mutableStateListOf(
            ScheduleUiModel(
                title = "Morning pump",
                startTime = "06:00",
                endTime = "07:00",
                days = listOf("Mo", "Tu", "We", "Th", "Fr"),
                isEnabled = true,
                duration = "1hr"
            ),
            ScheduleUiModel(
                title = "Evening pump",
                startTime = "18:00",
                endTime = "18:30",
                days = listOf("Sa", "Su"),
                isEnabled = false,
                duration = "30 min"
            )
        )
    }

    val newSchedule = Schedule(
        id = "", // Leave empty; Repository will fill this from Firestore
        title = "Morning Filling",
        timeWindow = TimeWindow(
            start = "06:00",
            end = "07:30"
        ),
        activeDays = listOf(1, 2, 3, 4, 5), // Mon to Fri
        settings = ScheduleSettings(
            isEnabled = true,
            untilFull = true
        ),
        syncStatus = SyncStatus.Pending // Digital Shadow starts as Pending
    )


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
            // --- FIXED SECTION ---
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                //TopBar("Pump Scheduler")
                Spacer(modifier = Modifier.height(16.dp))
                NextScheduleCard()
                Spacer(modifier = Modifier.height(24.dp))

                // HEADER ROW WITH ADD ICON
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
                            schedules.add(
                                ScheduleUiModel(
                                    title = "New Schedule",
                                    startTime = "00:00",
                                    endTime = "01:00",
                                    days = emptyList(),
                                    isEnabled = true,
                                    duration = "1hr"
                                )
                            )
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Schedule",
                            tint = ActiveBlue
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- SCROLLABLE SECTION ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(schedules.size) { index ->
                    val schedule = schedules[index]
                    key(schedule.id) {
                        ScheduleCard(
                            schedule = newSchedule,
                            onToggle = {
                                schedules[index] = schedule.copy(isEnabled = !schedule.isEnabled)
                            },
                            onDelete = {
                                schedules.removeAt(index)
                            }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
    }
}

// -------------------- COMPONENTS --------------------

@Composable
fun ScheduleCard(
    schedule: Schedule, // Use the Domain Model
    onToggle: (Boolean) -> Unit, // Pass the new state back
    onDelete: () -> Unit
) {
    // UI-only states
    var showDialog by remember { mutableStateOf(false) }

    // Check if we are in "Pending" state to dim the card slightly
    val isPending = schedule.syncStatus is SyncStatus.Pending

    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Visual feedback: Dim the card if the hardware hasn't synced the change
            .alpha(if (isPending) 0.8f else 1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(0.5.dp, if (isPending) ActiveBlue.copy(alpha = 0.5f) else CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(schedule.title, color = TextSecondary, fontSize = 12.sp)
                        Spacer(Modifier.width(8.dp))

                        // 🔥 DIGITAL SHADOW INDICATOR
                        SyncIndicator(status = schedule.syncStatus)
                    }

                    Text(
                        text = if (schedule.settings.untilFull)
                            "${schedule.timeWindow.start} → Full"
                        else "${schedule.timeWindow.start} → ${schedule.timeWindow.end}",
                        modifier = Modifier.clickable { showDialog = true },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                // Pass the current state to your toggle
                IOSToggle(
                    isOn = schedule.settings.isEnabled,
                    onToggle = { onToggle(!schedule.settings.isEnabled) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // DaysRow (using Int list from Domain)
            DaysRow(
                activeDays = schedule.activeDays,
                onDayClick = { /* Handle via ViewModel update */ }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Duration could be a property or extension on the Domain Model
                Text(
                    text = "Status: ${if(isPending) "Synchronizing..." else "Active"}",
                    color = if(isPending) Color.Red else ActiveBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onDelete() }
                )
            }
        }
    }

    // Dialog logic remains similar, but should trigger ViewModel updates onSave
    if (showDialog) {
        TimeRangeDialog(
            startTime = schedule.timeWindow.start,
            endTime = schedule.timeWindow.end ?: "",
            untilFull = schedule.settings.untilFull,
            onDismiss = { showDialog = false },
            onSave = { start, end, full ->
                // Call ViewModel to update the schedule object
                showDialog = false
            },
            onStartTimeChange = TODO(),
            onEndTimeChange = TODO(),
            onUntilFullChange = TODO()
        )
    }
}

@Composable
fun SyncIndicator(status: SyncStatus) {
    when (status) {
        is SyncStatus.Pending -> {
            // Show a small spinner indicating the "Shadow" hasn't synced yet
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = Color.Yellow
            )
        }
        is SyncStatus.Synced -> {
            // Confirmation that hardware has the data
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Synced",
                tint = ToggleGreen,
                modifier = Modifier.size(18.dp)
            )
        }
        is SyncStatus.Error -> {
            // Show error if ESP32 failed to apply it
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = status.message,
                tint = Color.Red,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

//@Composable
//fun ScheduleCard(
//    schedule: ScheduleUiModel,
//    onToggle: () -> Unit,
//    onDelete: () -> Unit
//) {
//    var showDialog by remember { mutableStateOf(false) }
//    var startTime by remember { mutableStateOf(schedule.startTime) }
//    var endTime by remember { mutableStateOf(schedule.endTime) }
//    var untilFull by remember { mutableStateOf(false) }
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = CardBg),
//        border = BorderStroke(0.5.dp, CardBorder)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            val selectedDays =
//                remember { mutableStateListOf<String>().apply { addAll(schedule.days) } }
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Column(modifier = Modifier.weight(1f)) {
//                    Text(schedule.title, color = TextSecondary, fontSize = 12.sp)
//                    Text(
//                        text = if (untilFull) "$startTime → Full" else "$startTime → $endTime",
//                        modifier = Modifier.clickable { showDialog = true },
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.SemiBold,
//                        color = TextPrimary
//                    )
//                }
//                IOSToggle(isOn = schedule.isEnabled, onToggle = onToggle)
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            DaysRow(
//                activeDays = selectedDays,
//                onDayClick = { day ->
//                    if (selectedDays.contains(day)) selectedDays.remove(day) else selectedDays.add(
//                        day
//                    )
//                }
//            )
//
//            // DELETE ICON BELOW DAYS
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Duration: ${schedule.duration}",
//                    color = ActiveBlue,
//                    fontSize = 13.sp,
//                    fontWeight = FontWeight.Medium
//                )
//
//                Icon(
//                    imageVector = Icons.Default.Delete,
//                    contentDescription = "Delete",
//                    tint = Color.Red.copy(alpha = 0.6f),
//                    modifier = Modifier
//                        .size(20.dp)
//                        .clickable { onDelete() }
//                )
//            }
//        }
//    }
//
//    if (showDialog) {
//        TimeRangeDialog(
//            startTime = startTime,
//            endTime = endTime,
//            untilFull = untilFull,
//            onStartTimeChange = { startTime = it },
//            onEndTimeChange = { endTime = it },
//            onUntilFullChange = { untilFull = it },
//            onDismiss = { showDialog = false },
//            onSave = { _, _, _ -> showDialog = false }
//        )
//    }
//}

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
fun DaysRow(
    activeDays: List<Int>, // Now using the Domain-friendly Int list
    onDayClick: (Int) -> Unit
) {
    val allDays = listOf(
        1 to "Mo", 2 to "Tu", 3 to "We",
        4 to "Th", 5 to "Fr", 6 to "Sa", 7 to "Su"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        allDays.forEach { (index, label) ->
            val isSelected = index in activeDays

            DayCircle(
                text = label,
                isActive = isSelected,
                onClick = { onDayClick(index) }
            )
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