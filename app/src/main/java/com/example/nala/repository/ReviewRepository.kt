package com.example.nala.repository

import com.example.nala.db.models.review.WordReviewModelDto
import com.example.nala.domain.model.dictionary.DictionaryModel

interface ReviewRepository {
    suspend fun addToReview(word: DictionaryModel)

    suspend fun getReviewItems() : List<DictionaryModel>

    suspend fun getNReviewItems(n: Int) : List<WordReviewModelDto>

    suspend fun getTodaysReviews() : List<WordReviewModelDto>

    suspend fun updateReviewParameters(quality: Int, wordReview: WordReviewModelDto)

    suspend fun mapReviewToDomain(wordReview: WordReviewModelDto) : DictionaryModel

    suspend fun mapReviewsToDomain(reviews: List<WordReviewModelDto>) : List<DictionaryModel>
}