package com.example.nala.db.models.review.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nala.db.models.review.KanjiMeanings
import com.example.nala.db.models.review.KanjiReviewModel


data class KanjiWithMeanings (
    @Embedded val kanji: KanjiReviewModel,
    @Relation(
        parentColumn="kanji",
        entityColumn="kanji"
    )
    val meanings: List<KanjiMeanings>
    )