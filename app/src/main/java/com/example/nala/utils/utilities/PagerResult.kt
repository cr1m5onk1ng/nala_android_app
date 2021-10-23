package com.example.nala.utils.utilities

data class PagerResult<K, T>(
    val data: Collection<T>,
    val nextKey: K,
    val hasReachedEnd: Boolean,
)
