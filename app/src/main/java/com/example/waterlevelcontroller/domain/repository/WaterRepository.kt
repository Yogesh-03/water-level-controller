package com.example.waterlevelcontroller.domain.repository
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.domain.model.Sensor
import kotlinx.coroutines.flow.Flow

interface WaterRepository {

    suspend fun setPump(state: String): Resource<Unit>

    fun observePump(): Flow<Resource<String>>

    fun observeSensors(): Flow<Resource<Sensor>>
}