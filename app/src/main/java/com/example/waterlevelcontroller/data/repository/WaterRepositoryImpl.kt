package com.example.waterlevelcontroller.data.repository

import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.data.mapper.toPumpControl
import com.example.waterlevelcontroller.data.mapper.toPumpControlDto
import com.example.waterlevelcontroller.data.mapper.toSensors
import com.example.waterlevelcontroller.data.model.dto.PumpControlDto
import com.example.waterlevelcontroller.domain.model.Sensor

import com.example.waterlevelcontroller.data.remote.FirebaseDataSource
import com.example.waterlevelcontroller.domain.model.PumpControl
import com.example.waterlevelcontroller.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WaterRepositoryImpl @Inject constructor(
    private val firebase: FirebaseDataSource
) : WaterRepository {


    override suspend fun updatePumpControl(data: PumpControl): Resource<Unit> {
        return try {
            firebase.updatePumpControl(data.toPumpControlDto())
            Resource.Success(Unit)
        } catch (e : Exception){
            Resource.Error(e.message ?: "Error")
        }
    }

    override fun observePumpControl(): Flow<Resource<PumpControl>> {
        return firebase.observePumpControl()
            .map {
                Resource.Success(it.toPumpControl())
            }

    }

    override fun observeWaterLevels(): Flow<Resource<Sensor>> {
        return firebase.observeWaterLevels()
            .map { Resource.Success(it.toSensors()) }
    }
}