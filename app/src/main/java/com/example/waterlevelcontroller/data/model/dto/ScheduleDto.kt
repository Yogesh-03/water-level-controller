package com.example.waterlevelcontroller.data.model.dto

data class ScheduleDto(
    val id: String? = null,
    val title: String = "",
    val startTime: String = "",
    val endTime: String? = null,
    val days: List<Int> = emptyList(), // Industry uses 1-7 for days
    val untilFull: Boolean = false,

    // THE DIGITAL SHADOW PART
    val isEnabled: Boolean = true,       // APP WRITES THIS
    val isSynced: Boolean = false,       // ESP32 WRITES THIS (True when it gets the update)
    val lastError: String? = null,       // ESP32 WRITES THIS (e.g., "Invalid Time")
    val version: Int = 1                 // APP INCREMENTS THIS on every save
)