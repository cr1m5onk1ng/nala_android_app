package com.example.nala.repository

import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.network.model.dictionary.SearchOutput

interface DictionaryRepository {
    suspend fun search(word: String) : DictionaryModel

    suspend fun tokenize(text: String) : List<String>
}