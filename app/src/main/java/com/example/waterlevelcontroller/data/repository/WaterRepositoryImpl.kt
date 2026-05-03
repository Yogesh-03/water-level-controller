package com.example.waterlevelcontroller.data.repository

import android.util.Log
import com.example.waterlevelcontroller.core.constants.FirebasePaths
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.data.mapper.toPumpControl
import com.example.waterlevelcontroller.data.mapper.toPumpControlDto
import com.example.waterlevelcontroller.data.mapper.toSensors
import com.example.waterlevelcontroller.data.model.dto.PumpControlDto
import com.example.waterlevelcontroller.domain.model.Sensor

import com.example.waterlevelcontroller.data.remote.FirebaseDataSource
import com.example.waterlevelcontroller.data.remote.PumpDataSource
import com.example.waterlevelcontroller.domain.model.PumpControl
import com.example.waterlevelcontroller.domain.model.PumpField
import com.example.waterlevelcontroller.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WaterRepositoryImpl @Inject constructor(
    private val pumpDataSource: PumpDataSource
) : WaterRepository {


    override fun observePumpControl(): Flow<Resource<PumpControl>> {
        return pumpDataSource.observePumpControl()
            .map {
                Resource.Success(it.toPumpControl())
            }

    }

    override fun observeWaterLevels(): Flow<Resource<Sensor>> {
        return pumpDataSource.observeWaterLevels()
            .map { Resource.Success(it.toSensors()) }
    }

    override suspend fun <T> updatePumpField(field: PumpField, value: T): Resource<Unit> {
        val path = when (field) {
            PumpField.STATE -> FirebasePaths.PUMP_STATE_DESIRED
            PumpField.MODE -> FirebasePaths.MODE_DESIRED
            PumpField.MANUAL_CONTROL -> FirebasePaths.MANUAL_PUMP_DESIRED
        }

        return try {
            pumpDataSource.updateSingleField(path, value)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sync Failed")
        }
    }

}