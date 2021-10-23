package com.example.nala.db.models.review

import androidx.room.*
import com.example.nala.db.converters.TimeConverter
import java.util.*

@Entity(
    tableName = "kanji_review",
    indices = [Index(value = ["added_at"]), Index(value=["interval"])]
)
data class KanjiReviewCache (
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

    @TypeConverters(TimeConverter::class)
    @ColumnInfo(name="added_at")
    val addedAt: Date = Date(),
    ) : ReviewModel()