package com.yuvraj.visionai.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.yuvraj.visionai.model.EyeTestResult
import kotlinx.coroutines.tasks.await


class EyeTestPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, EyeTestResult>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, EyeTestResult> {
        return try {
            val currentPage = params.key ?: query.get().await()
            val lastDocumentSnapshot = currentPage.documents.lastOrNull()
            val nextPage = if (lastDocumentSnapshot != null) {
                query.startAfter(lastDocumentSnapshot).get().await()
            } else {
                null
            }

            LoadResult.Page(
                data = currentPage.toObjects(EyeTestResult::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, EyeTestResult>): QuerySnapshot? {
        return null
    }
}
