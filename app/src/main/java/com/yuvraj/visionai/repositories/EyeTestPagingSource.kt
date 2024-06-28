package com.yuvraj.visionai.repositories

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.yuvraj.visionai.model.EyeTestResult
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import timber.log.Timber.*


class EyeTestPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, EyeTestResult>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, EyeTestResult> {
        Log.d("EyeTestPagingSourceDebug", "load: Paging Source")
        return try {
            val currentPage = params.key ?: query.limit(params.loadSize.toLong()).get().await()
            val lastVisibleDocument = currentPage.documents.lastOrNull()

            val nextPage = lastVisibleDocument?.let {
                query.startAfter(it).limit(params.loadSize.toLong()).get().await()
            }

            printLog("Data: ${currentPage.toObjects(EyeTestResult::class.java)}")

            LoadResult.Page(
                data = currentPage.toObjects(EyeTestResult::class.java),
                prevKey = null, // Only paging forward.
                nextKey = nextPage
            )
        } catch (e: Exception) {
            printLog("Error: ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, EyeTestResult>): QuerySnapshot? {
        return null
    }

    fun printLog(msg: String) {
        Log.d("EyeTestPagingSourceDebug",msg)
    }
}
