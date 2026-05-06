package com.example.waterlevelcontroller.data.repository

import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.data.mapper.toDto
import com.example.waterlevelcontroller.data.model.dto.ScheduleDto
import com.example.waterlevelcontroller.data.remote.ScheduleDataSource
import com.example.waterlevelcontroller.domain.model.Schedule
import com.example.waterlevelcontroller.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDataSource: ScheduleDataSource
) : ScheduleRepository {
    //    override fun observeSchedule(): Flow<Resource<Schedule>> {
//
//    }
//
    override suspend fun updateSchedule(
        scheduleId: String,
        userId: String,
        userName: String,
        scheduleDto: ScheduleDto
    ): Resource<Unit> {
        return try {
            scheduleDataSource.updateSchedule(scheduleId, scheduleDto)
            return Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Update failed")
        }
    }

    override suspend fun addSchedule(
        schedule: Schedule,
        userId: String,
        userName: String
    ): Resource<Unit> {
        return try {
            scheduleDataSource.addSchedule(schedule.toDto("Yogesh", "Yogesh Yadav"))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Schedule not added")
        }
    }
}