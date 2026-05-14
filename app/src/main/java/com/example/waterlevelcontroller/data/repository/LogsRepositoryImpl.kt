package com.example.waterlevelcontroller.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.data.local.AppDatabase
import com.example.waterlevelcontroller.data.mapper.toDomain
import com.example.waterlevelcontroller.data.model.dto.PumpLogsDto
import com.example.waterlevelcontroller.data.paging.PumpLogPagingSource
import com.example.waterlevelcontroller.data.remote.LogsDataSource
import com.example.waterlevelcontroller.data.remote.PumpLogRemoteMediator
import com.example.waterlevelcontroller.domain.model.PumpLogs
import com.example.waterlevelcontroller.domain.repository.LogsRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LogsRepositoryImpl @Inject constructor(
    private val logsDataSource: LogsDataSource,
    private val database: AppDatabase,
    private val firestore: FirebaseFirestore
) : LogsRepository {

    override suspend fun getPumpLogs(): Flow<Resource<List<PumpLogs>>> {
        return logsDataSource.getPumpLogs()
            .map { dtoList ->
                val logsList = dtoList.map { it.toDomain() }
                Resource.Success(logsList) as Resource<List<PumpLogs>>
            }
            .catch { e ->
                // This catches exceptions occurring in the flow or the data source
                emit(Resource.Error(e.message ?: "An unknown error occurred"))
            }
    }

    override fun getPumpLogsPaging(): Flow<PagingData<PumpLogs>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PumpLogPagingSource(Firebase.firestore) }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPumpLogsMediator(): Flow<PagingData<PumpLogs>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = PumpLogRemoteMediator(database, firestore),
            pagingSourceFactory = { database.pumpLogDao().pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }
}