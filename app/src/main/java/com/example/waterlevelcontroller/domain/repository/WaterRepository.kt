package com.example.waterlevelcontroller.domain.repository
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.data.model.dto.PumpControlDto
import com.example.waterlevelcontroller.domain.model.PumpControl
import com.example.waterlevelcontroller.domain.model.Sensor
import kotlinx.coroutines.flow.Flow

interface WaterRepository {

    suspend fun updatePumpControl(data: PumpControl): Resource<Unit>

     fun observePumpControl(): Flow<Resource<PumpControl>>

     fun observeWaterLevels(): Flow<Resource<Sensor>>
}