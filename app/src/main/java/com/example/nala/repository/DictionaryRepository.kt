package com.example.nala.repository

import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.network.model.dictionary.SearchOutput
import com.example.nala.utils.Resource

interface DictionaryRepository {
    suspend fun search(word: String) : DictionaryModel

    suspend fun tokenize(text: String) : List<String>

    fun tokensToIndexMap(tokens: List<String>, text: String): Map<Pair<Int, Int>, String>

}