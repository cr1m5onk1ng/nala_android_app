package com.example.nala.repository

import com.example.nala.domain.model.dictionary.DictionaryModel
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    suspend fun search(word: String) : DictionaryModel

    fun searchFlow(word: String) : Flow<DictionaryModel>

    suspend fun tokenize(text: String) : List<String>

    fun tokensToIndexMap(tokens: List<String>, text: String): Map<Pair<Int, Int>, String>

}