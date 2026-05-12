package com.example.waterlevelcontroller.domain.model

data class PumpLogs (
    val id: String = "",
    val consumption_liters: Double = 0.0,
    val level_at_start: Int = 0,
    val level_at_stop: Int = 0,
    val start_timestamp: Long = 0,
    val end_timestamp: Long = 0,
    val stop_reason: String = "",
    val user_id: String = ""
)