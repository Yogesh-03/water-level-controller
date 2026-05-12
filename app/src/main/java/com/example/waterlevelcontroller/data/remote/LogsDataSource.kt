package com.example.waterlevelcontroller.data.remote

import com.example.waterlevelcontroller.core.constants.FirebasePaths
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.data.model.dto.PumpLogsDto
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LogsDataSource @Inject constructor(
    private val firebase : FirebaseDataSource
) {
    fun getPumpLogs() : Flow<List<PumpLogsDto>>{
        return firebase.observeCollection(FirebasePaths.PUMP_LOGS).map {
            snapshot ->
            snapshot.toObjects(PumpLogsDto::class.java)
        }
    }
}