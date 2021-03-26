package com.example.nala.repository

import com.example.nala.domain.model.dictionary.DictionaryModel

interface DictionaryRepository {
    suspend fun search(word: String) : DictionaryModel

    suspend fun addToReview(word: DictionaryModel)

    suspend fun getReviewItems() : List<DictionaryModel>

    suspend fun getNReviewItems(n: Int) : List<DictionaryModel>
}