package com.example.nala.db.models.kanji

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji_dict")
data class KanjiDictDbModel (
    @PrimaryKey
    @ColumnInfo(name="kanji")
    val kanji: String,

    @ColumnInfo(name="freq")
    val freq: String?,

    @ColumnInfo(name="grade")
    val grade: String?,

    @ColumnInfo(name="jlpt")
    val jlpt: String?,

    @ColumnInfo(name="strokes")
    val strokes: String?,
)