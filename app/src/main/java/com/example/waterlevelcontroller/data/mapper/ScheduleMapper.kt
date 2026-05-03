package com.example.waterlevelcontroller.data.mapper

import com.example.waterlevelcontroller.data.model.dto.ScheduleDto
import com.example.waterlevelcontroller.data.model.dto.ScheduleStateDto
import com.example.waterlevelcontroller.domain.model.Schedule
import com.example.waterlevelcontroller.domain.model.ScheduleSettings
import com.example.waterlevelcontroller.domain.model.SyncStatus
import com.example.waterlevelcontroller.domain.model.TimeWindow

fun ScheduleDto.toDomain(): Schedule {
    // 1. Identify the 'active' data to show the user.
    // We prioritize 'desired' so the UI feels responsive (Optimistic UI).
    val activeData = desired ?: reported ?: ScheduleStateDto()

    // 2. Shadow Logic: Check if the hardware has acknowledged the request.
    // If desired matches reported, we are Synced. Otherwise, it's Pending.
    val isSynced = desired != null && reported != null && desired == reported

    return Schedule(
        id = id,
        title = title,
        timeWindow = TimeWindow(
            start = activeData.start,
            end = activeData.end
        ),
        activeDays = activeData.activeDays,
        settings = ScheduleSettings(
            isEnabled = activeData.isEnabled,
            untilFull = activeData.untilFull
        ),
        syncStatus = if (isSynced) SyncStatus.Synced else SyncStatus.Pending,
        createdBy = createdBy,
        lastEditedBy = lastEditedBy,
        lastEditedName = lastEditedName,
        lastUpdated = lastUpdated
    )
}

fun Schedule.toDto(userId: String, userName: String): ScheduleDto {
    return ScheduleDto(
        id = id,
        title = title,
        createdBy = createdBy.ifEmpty { userId }, // Keep original creator if exists
        lastEditedBy = userId,
        lastEditedName = userName,
        lastUpdated = System.currentTimeMillis(),
        // Map current UI state to 'desired' so the ESP32 sees the change
        desired = ScheduleStateDto(
            start = timeWindow.start,
            end = timeWindow.end,
            activeDays = activeDays,
            isEnabled = settings.isEnabled,
            untilFull = settings.untilFull
        ),
        // Leave reported as null; the repository update should ideally
        // use a Map or updateChildren to avoid overwriting the
        // existing 'reported' node in Firebase.
        reported = null
    )
}