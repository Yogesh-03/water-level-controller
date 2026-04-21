package com.example.waterlevelcontroller.domain.model

data class Schedule(
    val id: String = "",
    val title: String,
    val startTime: String,
    val endTime: String?,
    val days: List<String>,
    val isEnabled: Boolean,
    val untilFull: Boolean,
    val duration: String
)