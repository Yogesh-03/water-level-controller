package com.example.waterlevelcontroller.domain.usecase

import com.example.waterlevelcontroller.domain.repository.WaterRepository
import javax.inject.Inject

class GetSensorDataUseCase @Inject constructor(
    private val repository: WaterRepository
) {
    //operator fun invoke() = repository.observeSensors()
}