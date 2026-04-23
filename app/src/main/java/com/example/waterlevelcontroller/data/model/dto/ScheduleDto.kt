package com.example.waterlevelcontroller.data.model.dto


data class ScheduleDto(
    val id : String? = null,
    val title: String = "",
    val startTime: String = "",   // or better: Int (minutes)
    val endTime: String? = null,
    val days: List<String> = emptyList(),
    val isEnabled: Boolean = true,
    val untilFull: Boolean = false
)