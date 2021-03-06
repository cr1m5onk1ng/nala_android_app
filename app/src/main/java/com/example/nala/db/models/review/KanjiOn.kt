package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji_on", primaryKeys=["kanji", "onReading"])
data class KanjiOn (

    @ColumnInfo(name="kanji")
    val kanji: String,

    @ColumnInfo(name="onReading")
    val onReading: String,
)