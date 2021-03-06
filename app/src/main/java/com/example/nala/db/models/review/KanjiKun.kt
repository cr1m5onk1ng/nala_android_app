package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji_kun", primaryKeys = ["kanji", "kunReading"])
data class KanjiKun (

    @ColumnInfo(name="kanji")
    val kanji: String,

    @ColumnInfo(name="kunReading")
    val kunReading: String,
        )