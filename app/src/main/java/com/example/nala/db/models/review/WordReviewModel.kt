package com.example.nala.db.models.review


import androidx.room.*
import com.example.nala.db.converters.TimeConverter
import com.example.nala.db.models.DatabaseModel
import java.util.*

@Entity(
    tableName = "word_review",
    indices = [Index(value = ["added_at"]), Index(value=["interval"])]
)
data class WordReviewModel (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="word")
    val word: String,

    @ColumnInfo(name="reading")
    val reading: String,

    @ColumnInfo(name="jlpt")
    val jlpt: String? = null,

    @ColumnInfo(name="pos")
    val pos: String? = null,

    @ColumnInfo(name="common")
    val common: Boolean? = null,

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

@Entity(tableName = "word_review_fts")
@Fts4(contentEntity = WordReviewModel::class)
data class WordReviewModelFts(
    @ColumnInfo(name="word")
    val word: String,
)