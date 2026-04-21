package com.example.waterlevelcontroller.data.remote

import android.util.Log
import com.example.waterlevelcontroller.core.constants.FirebasePaths
import com.example.waterlevelcontroller.data.model.dto.PumpControlDto
import com.example.waterlevelcontroller.data.model.dto.SensorDto
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(
    private val db: DatabaseReference
) {

    suspend fun updatePumpControl(
        data: PumpControlDto
    ) {
        val updates = mutableMapOf<String, Any>()

        data.mode?.let { updates["mode"] = it }
        data.pumpState?.let { updates["pumpState"] = it }
        data.manualPump?.let { updates["manualPump"] = it }

        if (updates.isNotEmpty()) {
            db.updateChildren(updates).await()
        }
    }


     fun observeWaterLevels(): Flow<SensorDto> = callbackFlow {

        val ref = db

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val dto = SensorDto(
                    overheadLow = snapshot.child(FirebasePaths.OH_LOW).getValue(Boolean::class.java),
                    overheadHigh = snapshot.child(FirebasePaths.OH_HIGH).getValue(Boolean::class.java),
                    undergroundLow = snapshot.child(FirebasePaths.UG_LOW).getValue(Boolean::class.java),
                    undergroundHigh = snapshot.child(FirebasePaths.UG_HIGH).getValue(Boolean::class.java)
                )

                trySend(dto)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

     fun observePumpControl(): Flow<PumpControlDto> = callbackFlow {

        val ref = db

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val dto = PumpControlDto(
                    pumpState = snapshot.child(FirebasePaths.PUMP_STATE).getValue(Boolean::class.java),
                    mode = snapshot.child(FirebasePaths.MODE).getValue(String::class.java),
                    manualPump = snapshot.child(FirebasePaths.MANUAL_PUMP).getValue(String::class.java)
                )

                trySend(dto)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}