package com.example.nala.db.models.kanji

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="kanji_meanings", primaryKeys = ["kanji", "meaning"])
data class KanjiMeanings (

    @ColumnInfo(name="kanji")
    val kanji: String,

    @ColumnInfo(name="meaning")
    val meaning: String,
        )