package com.example.waterlevelcontroller.domain.model

/**
 * Clean Domain Model for the UI
 */
data class Schedule(
    val id: String,
    val title: String,
    val timeWindow: TimeWindow,
    val activeDays: List<Int>,
    val settings: ScheduleSettings,
    val syncStatus: SyncStatus,
    // Collaborative Tracking
    val createdBy: String,
    val lastEditedBy: String,
    val lastEditedName: String,
    val lastUpdated: Long
)

data class TimeWindow(
    val start: String, // e.g., "08:00"
    val end: String?   // e.g., "09:30" or null for "Until Full"
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