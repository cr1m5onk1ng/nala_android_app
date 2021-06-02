package com.example.nala.db.models.kanji

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="kanji_on_readings", primaryKeys = ["kanji", "onReading"])
data class KanjiOnReadings (

    @ColumnInfo(name="kanji")
    val kanji: String,

    @ColumnInfo(name="onReading")
    val onReading: String,
        )