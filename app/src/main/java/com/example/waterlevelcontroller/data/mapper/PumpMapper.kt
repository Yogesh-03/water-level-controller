package com.example.waterlevelcontroller.data.mapper

import com.example.waterlevelcontroller.data.local.PumpLogEntity
import com.example.waterlevelcontroller.data.model.dto.PumpControlDto
import com.example.waterlevelcontroller.data.model.dto.ScheduleDto
import com.example.waterlevelcontroller.data.model.dto.SensorDto
import com.example.waterlevelcontroller.domain.model.PumpControl
import com.example.waterlevelcontroller.domain.model.PumpLogs
import com.example.waterlevelcontroller.domain.model.Schedule
import com.example.waterlevelcontroller.domain.model.Sensor

fun PumpControlDto.toPumpControl(): PumpControl {
    return PumpControl(
        mode = mode ?: "auto",
        pumpState = pumpState ?: false,
        manualPump = manualPump ?: "off"
    )
}

fun SensorDto.toSensors(): Sensor {
    return Sensor(
        overheadHigh = overheadHigh ?: false,
        overheadLow = overheadLow ?: false,
        undergroundHigh = undergroundHigh ?: false,
        undergroundLow = undergroundLow ?: false,
        temperature = temperature ?: 0.0f,
        humidity = humidity ?: 0.0f
    )
}

fun PumpControl.toPumpControlDto(): PumpControlDto {
    return PumpControlDto (
        mode = mode,
        pumpState = pumpState,
        manualPump = manualPump
    )
}

/**
 * Converts Room Database Entity to Domain Model
 * This is what your UI/ViewModel will use.
 */
fun PumpLogEntity.toDomain(): PumpLogs {
    return PumpLogs(
        // Map fields exactly as they are named in your Domain model
        start_timestamp = this.startTimestamp,
        end_timestamp = this.endTimestamp,
        consumption_liters = this.consumptionLiters,
        stop_reason = this.stopReason
    )
}

/**
 * Optional: Converts Firestore DTO to Room Entity
 * Useful for the RemoteMediator logic.
 */
fun PumpLogs.toEntity(docId: String): PumpLogEntity {
    return PumpLogEntity(
        id = docId,
        startTimestamp = this.start_timestamp,
        endTimestamp = this.end_timestamp,
        consumptionLiters = this.consumption_liters,
        stopReason = this.stop_reason
    )
}

//fun ScheduleDto.toDomain(): Schedule {
//    return Schedule(
//        id = id ?: "",
//        title = title,
//        startTime = startTime,
//        endTime = endTime,
//        days = days,
//        isEnabled = isEnabled,
//        untilFull = untilFull,
//        duration = "5"
//    )
//}

//fun Schedule.toDto(): ScheduleDto {
//    return ScheduleDto(
//        title = title,
//        startTime = startTime,
//        endTime = endTime,
//        days = days,
//        isEnabled = isEnabled,
//        untilFull = untilFull
//    )
//}
