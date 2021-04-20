package com.example.nala.db.models.review.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nala.db.models.review.KanjiKun
import com.example.nala.db.models.review.KanjiReviewModel

data class KanjiWithKunReadings (
    @Embedded val kanji: KanjiReviewModel,
    @Relation(
        parentColumn="kanji",
        entityColumn="kanji"
    )
    val kunReadings: List<KanjiKun>
        )