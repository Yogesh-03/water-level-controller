package com.example.waterlevelcontroller.domain.model

data class Sensor(
    val overheadLow: Int = 0,
    val overheadHigh: Int = 0,
    val undergroundLow: Int = 0,
    val undergroundHigh: Int = 0,
    val pumpState: Int = 0
)