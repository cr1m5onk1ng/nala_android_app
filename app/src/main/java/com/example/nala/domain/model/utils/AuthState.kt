package com.example.nala.domain.model.utils

sealed class AuthState<out R> {

    data class Authenticated<out T>(val data: T) : AuthState<T>()
    data class Unauthenticated<out T>(val data: T) : AuthState<T>()
    data class AuthError(val type: ErrorType) : AuthState<Nothing>()

}
