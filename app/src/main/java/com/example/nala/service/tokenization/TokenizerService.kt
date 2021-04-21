package com.example.nala.service.tokenization

interface TokenizerService {
    suspend fun tokenize(text: String) : List<String>
}