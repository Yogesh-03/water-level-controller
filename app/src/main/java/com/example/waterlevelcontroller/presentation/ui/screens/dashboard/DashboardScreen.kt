package com.example.waterlevelcontroller.presentation.ui.screens.dashboard

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.presentation.ui.components.common.TopBar
import com.example.waterlevelcontroller.presentation.ui.theme.ActiveBlue
import com.example.waterlevelcontroller.presentation.ui.theme.BlueFill
import com.example.waterlevelcontroller.presentation.ui.theme.CardBg
import com.example.waterlevelcontroller.presentation.ui.theme.CardBorder
import com.example.waterlevelcontroller.presentation.ui.theme.DividerColor
import com.example.waterlevelcontroller.presentation.ui.theme.GreenDark
import com.example.waterlevelcontroller.presentation.ui.theme.GreenLight
import com.example.waterlevelcontroller.presentation.ui.theme.OrangeBg
import com.example.waterlevelcontroller.presentation.ui.theme.OrangeText
import com.example.waterlevelcontroller.presentation.ui.theme.PillGray
import com.example.waterlevelcontroller.presentation.ui.theme.ScreenBg
import com.example.waterlevelcontroller.presentation.ui.theme.StatBg
import com.example.waterlevelcontroller.presentation.ui.theme.TealFill
import com.example.waterlevelcontroller.presentation.ui.theme.TextPrimary
import com.example.waterlevelcontroller.presentation.ui.theme.TextSecondary
import com.example.waterlevelcontroller.presentation.ui.theme.ToggleGreen

import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(), navController: NavController
) {
    val waterLevelState by viewModel.waterLevelState.collectAsState()
    val pumpControlState by viewModel.pumpControlState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isUpdating = updateState is Resource.Loading
    val isOnline by viewModel.isOnline.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val isSyncing by viewModel.isSyncing.collectAsState(false)

    // --- SYNC FAILURE TOAST ---
    LaunchedEffect(updateState) {
        if (updateState is Resource.Error) {
            val errorMessage = (updateState as Resource.Error).message

            if (errorMessage?.contains("Hardware Timeout") == true) {
                // This triggers when the 10-second timer in ViewModel expires
                Toast.makeText(
                    context,
                    "Sync Failed: Motor not responding. Reverting changes...",
                    Toast.LENGTH_LONG
                ).show()
            } else if (errorMessage != null) {
                // General network or Firebase errors
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ScreenBg
    ) { padding ->

        // 🔥 ROOT BOX (important for blocking clicks)
        Box(modifier = Modifier.fillMaxSize()) {



            // 🔥 MAIN UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .alpha(if (isOnline) 1f else 0.5f) // dim UI when offline
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {

                    // 🔥 Top Bar (Live / Offline)
                    TopBar("Pump Controller", isOnline)

                    Spacer(Modifier.height(16.dp))

                    // ───────── WATER LEVEL ─────────
                    when (waterLevelState) {

                        is Resource.Loading -> {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = ActiveBlue)
                            }
                        }

                        is Resource.Error -> {
                            ErrorCard(
                                (waterLevelState as Resource.Error).message
                                    ?: "Error loading sensors"
                            )
                        }

                        is Resource.Success -> {
                            val w = (waterLevelState as Resource.Success).data
                            TanksRow(
                                ohHigh = w?.overheadHigh,
                                ohLow = w?.overheadLow,
                                ugHigh = w?.undergroundHigh,
                                ugLow = w?.undergroundLow
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // ───────── PUMP CONTROL ─────────
                    when (pumpControlState) {

                        is Resource.Loading -> {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = ActiveBlue)
                            }
                        }

                        is Resource.Error -> {
                            ErrorCard(
                                (pumpControlState as Resource.Error).message
                                    ?: "Error loading pump"
                            )
                        }

                        is Resource.Success -> {
                            val p = (pumpControlState as Resource.Success).data

                            PumpCard(
                                pumpState = p?.pumpState,
                                manualPump = p?.manualPump, // Pass the loading state here
                                onToggle = {
                                    val currentState = p?.pumpState ?: false
                                    viewModel.togglePump(!currentState)
                                    when(updateState){
                                        is Resource.Error<*> ->{

                                        }
                                        is Resource.Loading<*> -> {
                                            //Toast.makeText(context, "Synchronizing with hardware...", Toast.LENGTH_SHORT).show()
                                        }
                                        is Resource.Success<*> -> {
                                            //Toast.makeText(context, "Successfull", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                }
                            )

                            Spacer(Modifier.height(12.dp))

                            ModeCard(
                                mode = p?.mode,
                                onSwitchMode = {
                                    val nextMode =
                                        if (p?.mode == "auto") "manual" else "auto"
                                    viewModel.updateMode(nextMode)
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }

            // BLOCK ALL TOUCHES WHEN OFFLINE
            if (!isOnline) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { } // consume clicks
                )
            }

            if (isSyncing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(10f) // Ensure it's on top of everything
                        .pointerInput(Unit) {}
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Use a Key to ensure the animation is preserved during sync
                        key(isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(64.dp),
                                color = ActiveBlue,
                                strokeWidth = 5.dp, // 6dp is okay, but 5dp is smoother for 64dp size
                                strokeCap = StrokeCap.Round
                            )
                        }
                        Text(
                            text = "Synchronizing with Motor...",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}


// ─── Tanks Row ────────────────────────────────────────────────
@Composable
fun TanksRow(ohHigh: Boolean?, ohLow: Boolean?, ugHigh: Boolean?, ugLow: Boolean?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.weight(1f)) {
            TankCard(
                label = "Overhead",
                fillColor = BlueFill,
                isHigh = ohHigh,
                isLow = ohLow
            )
        }
        Box(Modifier.weight(1f)) {
            TankCard(
                label = "Underground",
                fillColor = TealFill,
                isHigh = ugHigh,
                isLow = ugLow
            )
        }
    }
}

@Composable
fun TankCard(label: String, fillColor: Color, isHigh: Boolean?, isLow: Boolean?) {
    val fillPercent = when {
        isHigh == true -> 0.9f
        isLow == true -> 0.5f
        else -> 0.1f
    }
    val percent = (fillPercent * 100).toInt()

    val animatedFill by animateFloatAsState(
        targetValue = fillPercent,
        animationSpec = tween(800, easing = EaseInOutCubic),
        label = "tankFill"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(0.5.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.6.sp
            )
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp)
                    .border(2.dp, Color(0xFFD1D1D6), RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF2F2F7))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(animatedFill)
                        .background(fillColor)
                        .align(Alignment.BottomCenter)
                )
            }

            Spacer(Modifier.height(6.dp))
            Text(
                " < $percent%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SensorPill("Low", isLow, 12.sp)
                SensorPill("High", isHigh, 12.sp)
            }
        }
    }
}

@Composable
fun SensorPill(label: String, isOn: Boolean?, fontSize: TextUnit) {
    val (bg, text) = when {
        label == "Low" && isOn == true -> OrangeBg to OrangeText
        isOn == true -> GreenLight to GreenDark
        else -> PillGray to TextSecondary
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(label, fontSize = fontSize, fontWeight = FontWeight.Medium, color = text)
    }
}

@Composable
fun PumpCard(
    pumpState: Boolean?,
    manualPump: String?,
    onToggle: () -> Unit
) {
    val isOn = pumpState == true

    Card(
        modifier = Modifier
            .fillMaxWidth(),
            // Visual cue: Card looks slightly "disabled" while loading
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(0.5.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isOn) GreenLight else PillGray),
                        contentAlignment = Alignment.Center
                    ) {
                        // If loading, you could replace the icon with a tiny spinner
                        // or just keep the PumpIcon
                        PumpIcon(isOn)
                    }
                    Column {
                        Text(
                            "Motor pump",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                             if (isOn) "Running" else "Stopped",
                            fontSize = 11.sp,
                            color = if (isOn) GreenDark else TextSecondary
                        )
                    }
                }

                // Disable the toggle while the API/Hardware is busy
                IOSToggle(
                    isOn = isOn,
                    onToggle = { onToggle() }
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBox(
                    label = "Pump state",
                    value = if (pumpState == true) "ON" else if (pumpState == false) "OFF" else "—",
                    valueColor = if (pumpState == true) GreenDark else TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Manual pump",
                    value = manualPump?.replaceFirstChar { it.uppercase() } ?: "—",
                    valueColor = if (manualPump == "on") GreenDark else TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
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
fun IOSToggle(
    isOn: Boolean,
    enabled: Boolean = true, // Add this
    onToggle: () -> Unit
) {
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
            .alpha(if (enabled) 1f else 0.5f) // Visual feedback for "busy"
            .background(if (isOn) ToggleGreen else Color(0xFFD1D1D6))
            .clickable(enabled = enabled) { onToggle() }, // Disable clicks while loading
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
fun StatBox(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(StatBg)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Text(label, fontSize = 10.sp, color = TextSecondary)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = valueColor)
        }
    }
}

// ─── Mode Card ────────────────────────────────────────────────
@Composable
fun ModeCard(mode: String?, onSwitchMode: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSwitchMode() }, // Make the whole card clickable
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(0.5.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Operating mode", fontSize = 11.sp, color = TextSecondary)
                Spacer(Modifier.height(2.dp))
                Text(
                    mode?.replaceFirstChar { it.uppercase() } ?: "No Data",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }
            ModeBadge(mode)
        }
    }
}

@Composable
fun ModeBadge(mode: String?) {
    val (bg, text, label) = when (mode) {
        "auto" -> Triple(GreenLight, GreenDark, "Auto")
        "manual" -> Triple(OrangeBg, OrangeText, "Manual")
        else -> Triple(PillGray, TextSecondary, "No Data")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = text)
    }
}

// ─── Error Card ───────────────────────────────────────────────
@Composable
fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        border = BorderStroke(0.5.dp, OrangeText.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            fontSize = 13.sp,
            color = OrangeText
        )
    }
}



