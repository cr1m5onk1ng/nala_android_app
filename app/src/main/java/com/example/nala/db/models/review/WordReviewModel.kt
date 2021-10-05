package com.example.nala.db.models.review


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.example.nala.db.models.DatabaseModel

@Entity(tableName = "word_review")
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
) : ReviewModel()

@Entity(tableName = "word_review_fts")
@Fts4(contentEntity = WordReviewModel::class)
data class WordReviewModelFts(
    @ColumnInfo(name="word")
    val word: String,
)