package com.example.waterlevelcontroller.data.mapper

import com.example.waterlevelcontroller.data.model.dto.PumpLogsDto
import com.example.waterlevelcontroller.domain.model.PumpLogs

fun PumpLogsDto.toDomain(): PumpLogs {
    return PumpLogs(
        id = id,
        consumption_liters = consumption_liters,
        level_at_start = level_at_start,
        level_at_stop = level_at_stop,
        start_timestamp = start_timestamp,
        end_timestamp = end_timestamp,
        stop_reason = stop_reason,
        user_id = user_id
    )
}

fun PumpLogs.toDto(): PumpLogsDto {
    return PumpLogsDto(
        id = id,
        consumption_liters = consumption_liters,
        level_at_start = level_at_start,
        level_at_stop = level_at_stop,
        start_timestamp = start_timestamp,
        end_timestamp = end_timestamp,
        stop_reason = stop_reason,
        user_id = user_id
    )
}