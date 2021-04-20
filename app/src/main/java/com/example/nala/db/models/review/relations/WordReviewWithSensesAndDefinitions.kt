package com.example.nala.db.models.review.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.db.models.review.WordSenseDb

data class WordReviewWithSensesAndDefinitions (
    @Embedded val wordReview: WordReviewModel,
    @Relation(
        entity=WordSenseDb::class,
        parentColumn="word",
        entityColumn="word"
    )
    val sensesWithDefinitions: List<WordSenseWithDefinitions>
        )
