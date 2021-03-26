package com.example.nala.db.models.review

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nala.db.models.DatabaseModel

data class WordAndTag (
    @Embedded val word: WordReviewModelDto,
    @Relation(
        parentColumn = "word",
        entityColumn = "word"
    )
    val tags: List<WordTagDto>
        ) : DatabaseModel()