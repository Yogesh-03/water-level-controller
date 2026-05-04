package com.example.waterlevelcontroller.data.remote

import android.util.Log
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
 *
 *   NOTE:
 * - No models should be used here
 * - No domain logic should exist in this layer
 */
class FirebaseDataSource @Inject constructor(
    private val db: DatabaseReference
) {
    fun observe(path: String): Flow<DataSnapshot> = callbackFlow {

        val ref = db.child(path)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun <T> setValue(path: String, value: T) {
        try {
            db.child(path).setValue(value).await()
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Update failed at $path", e)
            throw e
        }
    }


    fun generateId(path: String): String {
        return db.child(path).push().key ?: throw Exception("ID generation failed")
    }
}