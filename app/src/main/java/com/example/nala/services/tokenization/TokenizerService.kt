package com.example.nala.services.tokenization

interface TokenizerService {
    suspend fun tokenize(text: String) : List<String>
}