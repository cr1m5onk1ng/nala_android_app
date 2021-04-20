package com.example.nala.domain.util

interface AsyncDomainMapper <T, R>{

    suspend fun mapToDomainModel(model: T) : R

    fun mapFromDomainModel(model: R) : T
}