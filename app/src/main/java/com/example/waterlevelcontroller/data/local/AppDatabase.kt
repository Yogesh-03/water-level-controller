package com.example.waterlevelcontroller.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PumpLogEntity::class], // Add your entity here
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // This is the line you are likely missing!
    abstract fun pumpLogDao(): PumpLogDao

    // Professional Tip: Use a companion object for the database name
    companion object {
        const val DATABASE_NAME = "water_level_db"
    }
}