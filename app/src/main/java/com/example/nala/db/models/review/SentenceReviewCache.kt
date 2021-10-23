package com.example.nala.db.models.review

import androidx.room.*
import com.example.nala.db.converters.TimeConverter
import java.util.*

@Entity(
    tableName = "sentence_review", primaryKeys = ["sentence", "word"],
    indices = [Index(value = ["added_at"]), Index(value=["interval"])]
)
data class SentenceReviewCache(

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

    @TypeConverters(TimeConverter::class)
    @ColumnInfo(name="added_at")
    val addedAt: Date = Date(),
) : ReviewModel()

@Entity(tableName = "sentence_review_fts")
@Fts4(contentEntity = SentenceReviewCache::class)
data class SentenceReviewCacheFts(
    @ColumnInfo(name="sentence")
    val sentence: String,

    @ColumnInfo(name="word")
    val targetWord: String,
)