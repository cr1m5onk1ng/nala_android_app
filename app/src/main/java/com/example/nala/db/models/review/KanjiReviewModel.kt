package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji_review")
data class KanjiReviewModel (
    @PrimaryKey
    @ColumnInfo(name="kanji")
    val kanji: String,

    @ColumnInfo(name="freq")
    val freq: String?,

    @ColumnInfo(name="grade")
    val grade: String?,

    @ColumnInfo(name="jlpt")
    val jlpt: String?,

    @ColumnInfo(name="strokes")
    val strokes: String?,

    @ColumnInfo(name="repetitions")
    val repetitions: Int = 0,

    @ColumnInfo(name="ease_factor")
    val easeFactor: Double = 2.5,

    @ColumnInfo(name="interval")
    val interval: Int = 0,
    ) : ReviewModel()