package com.example.waterlevelcontroller.data.model.dto

/**
 * Represents the full Schedule object in Firebase Realtime Database.
 */
data class ScheduleDto(
    val id: String = "",
    val title: String = "",
    // Metadata for collaboration
    val createdBy: String = "",
    val lastEditedBy: String = "",
    val lastEditedName: String = "",
    val lastUpdated: Long = 0L,

    // Shadow Nodes
    val desired: ScheduleStateDto? = null,
    val reported: ScheduleStateDto? = null
)

/**
 * The specific hardware parameters that the ESP32 needs to process.
 */
data class ScheduleStateDto(
    val start: String = "",
    val end: String? = null,
    val activeDays: List<Int> = emptyList(),
    val isEnabled: Boolean = false,
    val untilFull: Boolean = false
)