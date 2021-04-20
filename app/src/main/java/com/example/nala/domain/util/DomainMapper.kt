package com.example.nala.domain.util

interface DomainMapper <T, R>{

    fun mapToDomainModel(model: T) : R

    fun mapFromDomainModel(model: R) : T
}