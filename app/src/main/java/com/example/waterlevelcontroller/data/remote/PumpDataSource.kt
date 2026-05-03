package com.example.waterlevelcontroller.data.remote

import com.example.waterlevelcontroller.core.constants.FirebasePaths
import com.example.waterlevelcontroller.data.model.dto.PumpControlDto
import com.example.waterlevelcontroller.data.model.dto.SensorDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PumpDataSource @Inject constructor(
    private val firebase: FirebaseDataSource
) {
    fun observeWaterLevels(): Flow<SensorDto> {
        return firebase.observe(FirebasePaths.SENSORS)
            .map { snapshot ->

                // Used partial paths here
                SensorDto(
                    overheadLow = snapshot.child(FirebasePaths.SENSORS_OH_LOW)
                        .getValue(Boolean::class.java),

                    overheadHigh = snapshot.child(FirebasePaths.SENSORS_OH_HIGH)
                        .getValue(Boolean::class.java),

                    undergroundLow = snapshot.child(FirebasePaths.SENSORS_UG_LOW)
                        .getValue(Boolean::class.java),

                    undergroundHigh = snapshot.child(FirebasePaths.SENSORS_UG_HIGH)
                        .getValue(Boolean::class.java),

                    humidity = snapshot.child(FirebasePaths.SENSORS_HUMIDITY)
                        .getValue(Float::class.java),

                    temperature = snapshot.child(FirebasePaths.SENSORS_TEMPERATURE)
                        .getValue(Float::class.java)
                )

            }

    }

    fun observePumpControl(): Flow<PumpControlDto> {
        return firebase.observe(FirebasePaths.PUMP_CONTROLS)
            .map { snapshot ->

                // Used Partial Paths here
                PumpControlDto(
                    pumpState = snapshot.child(FirebasePaths.PUMP_CONTROLS_PUMP_STATE_REPORTED)
                        .getValue(Boolean::class.java),

                    mode = snapshot.child(FirebasePaths.PUMP_CONTROLS_MODE_REPORTED)
                        .getValue(String::class.java),

                    manualPump = snapshot.child(FirebasePaths.PUMP_CONTROLS_MANUAL_PUMP_REPORTED)
                        .getValue(String::class.java)
                )
            }
    }

    suspend fun <T> updateSingleField(path: String, value: T) {
        firebase.setValue(path, value)
    }



}