package com.example.nala.db.models.review.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.db.models.review.WordSenseDb

data class WordReviewWithSenses (
    @Embedded
    val wordReview: WordReviewModel,

    @Relation(
        parentColumn="word",
        entityColumn="word"
    )
    val wordReviewSenses: List<WordSenseDb>
)