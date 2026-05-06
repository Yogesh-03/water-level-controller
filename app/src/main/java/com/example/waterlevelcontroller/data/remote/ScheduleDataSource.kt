package com.example.waterlevelcontroller.data.remote

import com.example.waterlevelcontroller.core.constants.FirebasePaths
import com.example.waterlevelcontroller.data.model.dto.ScheduleDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScheduleDataSource @Inject constructor(
    private val firebase: FirebaseDataSource
){
    suspend fun addSchedule(scheduleDto: ScheduleDto){
        val id = firebase.generateId("schedules")
        firebase.setValue("schedules/$id", scheduleDto.copy(id = id))
    }

    suspend fun updateSchedule(scheduleId : String, scheduleDto: ScheduleDto) {
        firebase.setValue("${FirebasePaths.SCHEDULES}/$scheduleId", scheduleDto)
    }

    fun observeSchedule() : Flow<List<ScheduleDto>> {
        return firebase.observe(FirebasePaths.SCHEDULES)
            .map { snapshot ->
                snapshot.children.mapNotNull { child ->
                    child.getValue(ScheduleDto::class.java)
                }
            }
    }

    suspend fun deleteSchedule(scheduleId : String){
        firebase.deletePath("${FirebasePaths.SCHEDULES}/$scheduleId")
    }
}