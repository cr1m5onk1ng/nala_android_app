package com.example.nala.db.models.review.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nala.db.models.review.WordDefinition
import com.example.nala.db.models.review.WordReviewModel


data class WordWithDefinitions (
    @Embedded val word: WordReviewModel,
    @Relation(
        parentColumn = "word",
        entityColumn = "word"
    )
    val definitions: List<WordDefinition>
        )