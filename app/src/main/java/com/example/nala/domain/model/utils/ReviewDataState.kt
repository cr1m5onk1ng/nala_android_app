package com.example.nala.domain.model.utils

sealed class ReviewDataState<out R> {
    data class Initial<out T>(val data: T) : ReviewDataState<T>()

    data class Success<out T>(val data: T) : ReviewDataState<T>()

    data class Error(val type: ErrorType) : ReviewDataState<Nothing>()

    data class Search<out T>(val data: T) : ReviewDataState<T>()

    object Loading: ReviewDataState<Nothing>()
}
