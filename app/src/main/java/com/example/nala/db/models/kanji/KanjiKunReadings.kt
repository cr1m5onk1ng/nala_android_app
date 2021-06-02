package com.example.nala.db.models.kanji

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="kanji_kun_readings", primaryKeys = ["kanji", "kunReading"])
data class KanjiKunReadings (

    @ColumnInfo(name="kanji")
    val kanji: String,

    @ColumnInfo(name="kunReading")
    val kunReading: String,
)