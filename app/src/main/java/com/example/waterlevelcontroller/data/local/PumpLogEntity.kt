package com.example.waterlevelcontroller.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pump_logs")
data class PumpLogEntity(
    @PrimaryKey val id: String, // Firestore document ID
    val startTimestamp: Long,
    val endTimestamp: Long,
    val consumptionLiters: Double,
    val stopReason: String
)