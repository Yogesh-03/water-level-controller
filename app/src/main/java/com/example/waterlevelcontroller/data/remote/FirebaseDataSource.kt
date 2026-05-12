package com.example.waterlevelcontroller.data.remote

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.collections.remove

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
    private val db: DatabaseReference,
    private val firestore : FirebaseFirestore
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

    suspend fun deletePath(path: String) {
        try {
            db.child(path).removeValue().await()
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Delete failed at $path", e)
            throw e
        }
    }


   //--------------------------------  FIRESTORE FUNCTIONS -------------------------------------
    /**
     * Observes a Firestore collection.
     * Returns a QuerySnapshot which the Repository can then map to models.
     */
    fun observeCollection(collectionPath: String, userId: String): Flow<com.google.firebase.firestore.QuerySnapshot> = callbackFlow {
        val query = firestore.collection(collectionPath)
            .whereEqualTo("user_id", userId)
            // Note: You might need a Firestore Index for this if you use .orderBy()
            .orderBy("start_timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(snapshot)
            }
        }

        awaitClose { registration.remove() }
    }



    suspend fun <T> setValueFirestore(path: String, value: T) {
        try {
            db.child(path).setValue(value).await()
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Update failed at $path", e)
            throw e
        }
    }

    fun generateIdFirestore(path: String): String = db.child(path).push().key ?: throw Exception("ID generation failed")

    suspend fun deletePathFirestore(path: String) {
        try {
            db.child(path).removeValue().await()
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Delete failed at $path", e)
            throw e
        }
    }
}


