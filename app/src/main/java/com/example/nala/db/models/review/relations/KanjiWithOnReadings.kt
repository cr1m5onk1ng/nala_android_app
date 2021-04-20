package com.example.nala.db.models.review.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nala.db.models.review.KanjiOn
import com.example.nala.db.models.review.KanjiReviewModel

data class KanjiWithOnReadings (
    @Embedded val kanji: KanjiReviewModel,
    @Relation(
        parentColumn="kanji",
        entityColumn="kanji"
    )
    val onReadings: List<KanjiOn>
        )