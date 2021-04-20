package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji_definitions")
data class KanjiMeanings (

    @PrimaryKey
    @ColumnInfo(name="kanji")
    val kanji: String,

    @ColumnInfo(name="definition")
    val meaning: String,
    )