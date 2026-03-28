package com.example.waterlevelcontroller.domain.usecase

import com.example.waterlevelcontroller.domain.repository.WaterRepository

class SetPumpUseCase  constructor(
    private val repository: WaterRepository
) {
    suspend operator fun invoke(state: String) =
        repository.setPump(state)
}