package com.example.nala.domain.util

interface NetworkMapper<K, E> {

    fun mapToDomainModel(networkModel: K) : E

}