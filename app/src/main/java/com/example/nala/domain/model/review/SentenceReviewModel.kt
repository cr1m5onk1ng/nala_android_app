package com.example.nala.domain.model.review

import com.example.nala.db.models.review.ReviewModel
import com.example.nala.domain.model.DomainModel

data class SentenceReviewModel (
    val sentence: String,
    val targetWord: String
        )