

package io.com.example.healthguard.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.squareup.sqldelight.android.paging3.QueryPagingSource
import data.Result
import io.com.example.healthguard.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class ResultRepository(private val database: Database) {
//    private val _query = database.resultsQueries
//    fun fetchResults(): Flow<PagingData<Result>> = Pager(
//        config = PagingConfig(
//            pageSize = 15,
//            enablePlaceholders = false,
//        ),
//        pagingSourceFactory = {
//            QueryPagingSource(
//                countQuery = _query.countResults(),
//                transacter = _query,
//                dispatcher = Dispatchers.IO,
//                queryProvider = _query::results,
//            )
//        },
//    ).flow
}
