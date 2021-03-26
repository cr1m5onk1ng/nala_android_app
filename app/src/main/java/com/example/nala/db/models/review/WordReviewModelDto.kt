package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nala.db.models.DatabaseModel

@Entity(tableName = "word_review")
data class WordReviewModelDto (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="word")
    val word: String,

    @ColumnInfo(name="reading")
    val reading: String,

    @ColumnInfo(name="jlpt")
    val jlpt: String?,

    @ColumnInfo(name="pos")
    val pos: String?,

    @ColumnInfo(name="common")
    val common: Boolean?,
) : DatabaseModel()