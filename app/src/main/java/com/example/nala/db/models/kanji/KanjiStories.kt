package com.example.nala.db.models.kanji

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji_stories")
data class KanjiStories(
    @PrimaryKey
    val kanji: String,

    @ColumnInfo(name="story")
    val story: String,
)