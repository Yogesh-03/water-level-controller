package com.example.waterlevelcontroller.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.waterlevelcontroller.data.local.AppDatabase
import com.example.waterlevelcontroller.data.local.PumpLogEntity
import com.example.waterlevelcontroller.domain.model.PumpLogs
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

@OptIn(ExperimentalPagingApi::class)
class PumpLogRemoteMediator(
    private val database: AppDatabase,
    private val firestore: FirebaseFirestore
) : RemoteMediator<Int, PumpLogEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PumpLogEntity>
    ): MediatorResult {
        return try {
            val lastTimestamp = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    // Fetch the latest timestamp we have in Room
                    database.pumpLogDao().getLastTimestamp() ?: 0L
                }
            }

            // Query Firestore for logs NEWER than our last local log
            val query = firestore.collection("pump_logs")
                .orderBy("start_timestamp")
                .startAfter(lastTimestamp ?: 0L)
                .limit(state.config.pageSize.toLong())

            val snapshot = query.get().await()
            val entities = snapshot.map { doc ->
                val log = doc.toObject(PumpLogs::class.java)
               if (log!=null){
                   PumpLogEntity(
                       id = doc.id,
                       startTimestamp = log.start_timestamp,
                       endTimestamp = log.end_timestamp,
                       consumptionLiters = log.consumption_liters,
                       stopReason = log.stop_reason
                   )
               } else null
            }.filterNotNull()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // Optional: database.pumpLogDao().clearAll()
                }
                database.pumpLogDao().insertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = entities.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}