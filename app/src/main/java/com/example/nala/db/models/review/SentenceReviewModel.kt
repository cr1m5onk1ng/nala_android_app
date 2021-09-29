package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "sentence_review", primaryKeys = ["sentence", "word"])
data class SentenceReviewModel(

    @ColumnInfo(name="sentence")
    val sentence: String,

    @ColumnInfo(name="word")
    val targetWord: String,

    @ColumnInfo(name="repetitions")
    val repetitions: Int = 0,

    @ColumnInfo(name="ease_factor")
    val easeFactor: Double = 2.5,

    @ColumnInfo(name="interval")
    val interval: Int = 0,
) : ReviewModel()

@Entity(tableName = "sentence_review_fts")
@Fts4(contentEntity = SentenceReviewModel::class)
data class SentenceReviewModelFts(
    @ColumnInfo(name="sentence")
    val sentence: String,

    @ColumnInfo(name="word")
    val targetWord: String,
)