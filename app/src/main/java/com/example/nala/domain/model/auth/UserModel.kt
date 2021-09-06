package com.example.nala.domain.model.auth

data class UserModel(
    val username: String,
    val email: String? = null,
    val token: String? = null,
)