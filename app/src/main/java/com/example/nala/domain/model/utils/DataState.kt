package com.example.nala.domain.model.utils

sealed class DataState<out R> {

    data class Initial<out T>(val data: T) : DataState<T>()

    data class Success<out T>(val data: T) : DataState<T>()

    data class Error(val type: ErrorType) : DataState<Nothing>()

    object Loading: DataState<Nothing>()

}