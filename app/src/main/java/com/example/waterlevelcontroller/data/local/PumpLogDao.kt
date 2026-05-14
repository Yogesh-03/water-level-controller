package com.example.waterlevelcontroller.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PumpLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<PumpLogEntity>)

    @Query("SELECT * FROM pump_logs ORDER BY startTimestamp DESC")
    fun pagingSource(): PagingSource<Int, PumpLogEntity>

    @Query("SELECT MAX(startTimestamp) FROM pump_logs")
    suspend fun getLastTimestamp(): Long?

    @Query("DELETE FROM pump_logs")
    suspend fun clearAll()
}