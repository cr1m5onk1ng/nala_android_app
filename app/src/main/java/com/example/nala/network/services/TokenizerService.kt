package com.example.nala.network.services

interface TokenizerService {
    suspend fun tokenize(word: String) : List<String>
}