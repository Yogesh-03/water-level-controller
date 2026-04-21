    package com.example.waterlevelcontroller.domain.model


    data class Sensor(
        val overheadHigh: Boolean,
        val overheadLow: Boolean,
        val undergroundHigh: Boolean,
        val undergroundLow: Boolean
    )