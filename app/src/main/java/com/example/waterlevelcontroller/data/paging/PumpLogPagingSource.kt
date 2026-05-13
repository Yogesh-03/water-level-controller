package com.example.waterlevelcontroller.data.paging

import android.graphics.pdf.LoadParams
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.waterlevelcontroller.domain.model.PumpLogs
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class PumpLogPagingSource(
    private val db: FirebaseFirestore
) : PagingSource<QuerySnapshot, PumpLogs>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, PumpLogs>): QuerySnapshot? = null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PumpLogs> {
        return try {
            // 1. Create the base query
            var query = db.collection("pump_logs")
                .orderBy("start_timestamp", Query.Direction.DESCENDING)
                .limit(params.loadSize.toLong())

            // 2. Use the "key" (last snapshot) to get the next page
            params.key?.let { lastSnapshot ->
                val lastDocument = lastSnapshot.documents.lastOrNull()
                if (lastDocument != null) {
                    query = query.startAfter(lastDocument)
                }
            }

            val result = query.get().await()
            val logs = result.toObjects(PumpLogs::class.java)

            LoadResult.Page(
                data = logs,
                prevKey = null, // Firestore doesn't support easy reverse paging
                nextKey = if (logs.size < params.loadSize) null else result
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}