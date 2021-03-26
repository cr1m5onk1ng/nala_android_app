package com.example.nala.domain.model.review

import com.example.nala.domain.model.dictionary.DictionaryModel

data class WordReviewModel(
    val id: String,
    val word: DictionaryModel,
    val timeAdded: String,
    val priority: Float
)