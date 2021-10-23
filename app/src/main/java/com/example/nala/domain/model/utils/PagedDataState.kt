package com.example.nala.domain.model.utils

sealed class PagedDataState<out K, out R> {
    object Loading : PagedDataState<Nothing, Nothing>()
    object LoadingNext: PagedDataState<Nothing, Nothing>()
    data class Initial<out D, out T>(val data: T, val page: D): PagedDataState<D, T>()
    data class Success<out D, out T>(val data: T, val page: D) : PagedDataState<D, T>()
    data class Error(val type: ErrorType) : PagedDataState<Nothing, Nothing>()
}
