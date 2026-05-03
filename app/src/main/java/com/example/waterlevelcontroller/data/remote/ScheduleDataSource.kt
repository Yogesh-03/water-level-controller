package com.example.waterlevelcontroller.data.remote

import com.example.waterlevelcontroller.data.model.dto.ScheduleDto
import javax.inject.Inject

class ScheduleDataSource @Inject constructor(
    private val firebase: FirebaseDataSource
){
    suspend fun addSchedule(scheduleDto: ScheduleDto, userId : String, userName : String){
        val id = firebase.generateId("schedules")
        firebase.setValue("schedules/$id", scheduleDto.copy(id = id))
    }
}