package com.example.nala.utils.utilities

import kotlinx.coroutines.flow.Flow

interface KeysetPager<K, T> {

    fun setKey(key: K)

    fun setLimit(limit: Int)

    suspend fun getNextResult() : Flow<PagerResult<K, T>>
}