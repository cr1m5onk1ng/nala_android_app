package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "sentence_review", primaryKeys = ["sentence", "word"])
data class SentenceReviewModelDto(

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