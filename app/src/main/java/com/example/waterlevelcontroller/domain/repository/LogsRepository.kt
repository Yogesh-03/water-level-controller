package com.example.waterlevelcontroller.domain.repository

import androidx.paging.PagingData
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.domain.model.PumpLogs
import kotlinx.coroutines.flow.Flow

interface LogsRepository {
    suspend fun getPumpLogs() : Flow<Resource<List<PumpLogs>>>

    fun getPumpLogsPaging(): Flow<PagingData<PumpLogs>>

    fun getPumpLogsMediator(): Flow<PagingData<PumpLogs>>
}