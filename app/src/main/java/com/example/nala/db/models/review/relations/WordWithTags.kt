package com.example.nala.db.models.review.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nala.db.models.DatabaseModel
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.db.models.review.WordTag

data class WordWithTags (
    @Embedded val word: WordReviewModel,
    @Relation(
        parentColumn = "word",
        entityColumn = "word"
    )
    val tags: List<WordTag>
        ) : DatabaseModel()