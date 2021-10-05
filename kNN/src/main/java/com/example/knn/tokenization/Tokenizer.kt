package com.example.knn.tokenization

interface Tokenizer {
    fun tokenize(text: String) : LongArray
}