package com.example.nala.ui

sealed class DataState<out R> {

    data class Initial<out T>(val data: T) : DataState<T>()

    data class Success<out T>(val data: T) : DataState<T>()

    data class Error(val message: String) : DataState<Nothing>()

    object Loading: DataState<Nothing>()


}