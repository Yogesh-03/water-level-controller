package com.example.waterlevelcontroller.domain.model

data class Schedule(
    val id: String,
    val title: String,
    val timeWindow: TimeWindow,
    val activeDays: List<Int>,
    val settings: ScheduleSettings,
    val syncStatus: SyncStatus
)

data class TimeWindow(
    val start: String,
    val end: String?
)

data class ScheduleSettings(
    val isEnabled: Boolean,
    val untilFull: Boolean
)

sealed class SyncStatus {
    object Synced : SyncStatus()
    object Pending : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}