package com.example.nala.services.auth

import com.example.nala.domain.model.auth.UserModel

interface Authenticator {

    fun signIn()

    fun signOut()
}