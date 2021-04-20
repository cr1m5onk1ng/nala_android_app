package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName="word_sense",
    /*
    foreignKeys = [
        ForeignKey(
            entity = WordReviewModel::class,
            parentColumns=arrayOf("word"),
            childColumns=arrayOf("word"),
            onDelete=ForeignKey.CASCADE
        )
    ]*/
)
data class WordSenseDb (
    @PrimaryKey(autoGenerate=false)
    @ColumnInfo(name="senseId")
    val senseId: String,

    @ColumnInfo(name="word")
    val word: String,

    @ColumnInfo(name="pos")
    val pos: String,

)