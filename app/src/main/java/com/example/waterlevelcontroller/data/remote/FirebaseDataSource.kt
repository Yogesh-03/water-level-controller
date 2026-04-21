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

/**
 * 🔥 FirebaseDataSource
 *
 * This class is responsible for:
 * - Communicating with Firebase Realtime Database
 * - Performing read/write operations
 * - Converting raw snapshot data → DTOs
 *
 *   NOTE:
 * - Only Data layer models (DTOs) should be used here
 * - No domain logic should exist in this layer
 */
class FirebaseDataSource @Inject constructor(
    private val db: DatabaseReference
) {

    /**
     *  Update Pump Control Fields
     *
     * Updates pump-related fields in Firebase:
     * - mode
     * - pumpState
     * - manualPump
     *
     *  Logic:
     * - Uses safe defaults if values are null
     * - Uses updateChildren → only updates specified fields (partial update)
     *
     * ⚠Important:
     * - Does NOT overwrite entire node
     * - Prevents accidental data loss
     * @param PumpControlDto
     */
    suspend fun updatePumpControl(data: PumpControlDto) {

        // 🔹 Prepare update map (partial update)
        val updates = mapOf(
            "mode" to (data.mode ?: "Auto"),
            "pumpState" to (data.pumpState ?: false),
            "manualPump" to (data.manualPump ?: "Off")
        )

        try {
            // 🔹 Push update to Firebase
            db.updateChildren(updates).await()

        } catch (e: Exception) {
            // ❌ Log + rethrow for upper layers (repository/viewmodel)
            Log.e("FirebaseDataSource", "Update failed", e)
            throw e
        }
    }


    /**
     *  Observe Water Level Sensors
     *
     * Emits real-time updates for:
     * - Overhead tank (Low/High)
     * - Underground tank (Low/High)
     *
     *  Uses Flow:
     * - Converts Firebase callback → Kotlin Flow
     * - Emits new values whenever data changes
     *
     *  Flow Type:
     * - Cold Flow (starts when collected)
     */
    fun observeWaterLevels(): Flow<SensorDto> = callbackFlow {

        val ref = db

        // Firebase Listener
        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                /**
                 * 🔄 Mapping Snapshot → DTO
                 *
                 * Reads values safely (nullable)
                 * Firebase may return null if key doesn't exist
                 */
                val dto = SensorDto(
                    overheadLow = snapshot.child(FirebasePaths.OH_LOW)
                        .getValue(Boolean::class.java),

                    overheadHigh = snapshot.child(FirebasePaths.OH_HIGH)
                        .getValue(Boolean::class.java),

                    undergroundLow = snapshot.child(FirebasePaths.UG_LOW)
                        .getValue(Boolean::class.java),

                    undergroundHigh = snapshot.child(FirebasePaths.UG_HIGH)
                        .getValue(Boolean::class.java)
                )

                // 📤 Emit data to Flow
                trySend(dto)
            }

            override fun onCancelled(error: DatabaseError) {
                // ❌ Close flow with error
                close(error.toException())
            }
        }

        //  Start listening
        ref.addValueEventListener(listener)

        //  Remove listener when Flow is cancelled
        awaitClose {
            ref.removeEventListener(listener)
        }
    }


    /**
     *  Observe Pump Control State
     *
     * Emits real-time updates for:
     * - Pump ON/OFF state
     * - Mode (Auto/Manual)
     * - Manual pump trigger
     *
     *  Real-time sync with Firebase
     */
    fun observePumpControl(): Flow<PumpControlDto> = callbackFlow {

        val ref = db

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                /**
                 * 🔄 Snapshot → PumpControlDto
                 */
                val dto = PumpControlDto(
                    pumpState = snapshot.child(FirebasePaths.PUMP_STATE)
                        .getValue(Boolean::class.java),

                    mode = snapshot.child(FirebasePaths.MODE)
                        .getValue(String::class.java),

                    manualPump = snapshot.child(FirebasePaths.MANUAL_PUMP)
                        .getValue(String::class.java)
                )

                // 📤 Emit updated state
                trySend(dto)
            }

            override fun onCancelled(error: DatabaseError) {
                // ❌ Propagate error
                close(error.toException())
            }
        }

        // Attach listener
        ref.addValueEventListener(listener)

        // Cleanup when Flow collector is gone
        awaitClose {
            ref.removeEventListener(listener)
        }
    }
}