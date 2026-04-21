package com.example.waterlevelcontroller.data.mapper

import com.example.waterlevelcontroller.data.model.dto.PumpControlDto
import com.example.waterlevelcontroller.data.model.dto.ScheduleDto
import com.example.waterlevelcontroller.data.model.dto.SensorDto
import com.example.waterlevelcontroller.domain.model.PumpControl
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
        undergroundLow = undergroundLow ?: false
    )
}

fun PumpControl.toPumpControlDto(): PumpControlDto {
    return PumpControlDto (
        mode = mode,
        pumpState = pumpState,
        manualPump = manualPump
    )
}

fun ScheduleDto.toDomain(): Schedule {
    return Schedule(
        id = id ?: "",
        title = title,
        startTime = startTime,
        endTime = endTime,
        days = days,
        isEnabled = isEnabled,
        untilFull = untilFull,
        duration = duration
    )
}

fun Schedule.toDto(): ScheduleDto {
    return ScheduleDto(
        title = title,
        startTime = startTime,
        endTime = endTime,
        days = days,
        isEnabled = isEnabled,
        untilFull = untilFull,
        duration = duration
    )
}
