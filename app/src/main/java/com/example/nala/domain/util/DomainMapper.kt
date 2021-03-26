package com.example.nala.domain.util

interface DomainMapper <T, DomainModel>{

    fun mapToDomainModel(model: T) : DomainModel

    fun mapFromDomainModel(model: DomainModel) : T
}