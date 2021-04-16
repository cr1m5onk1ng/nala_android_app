package com.example.nala.repository

import com.example.nala.db.models.review.WordReviewModelDto
import com.example.nala.domain.model.dictionary.DictionaryModel

interface DictionaryRepository {
    suspend fun search(word: String) : DictionaryModel
}