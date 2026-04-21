package com.example.waterlevelcontroller.domain.model

data class PumpControl(
    val mode: String,
    val pumpState: Boolean,
    val manualPump: String
)