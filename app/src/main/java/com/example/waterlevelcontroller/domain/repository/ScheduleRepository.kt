package com.example.waterlevelcontroller.domain.repository

import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.data.model.dto.ScheduleDto
import com.example.waterlevelcontroller.domain.model.Schedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

//    fun observeSchedule() : Flow<Resource<Schedule>>
//
    suspend fun updateSchedule(scheduleId : String, userId: String, userName: String, scheduleDto: ScheduleDto ) : Resource<Unit>
    suspend fun addSchedule(schedule: Schedule, userId : String, userName : String) : Resource<Unit>
}