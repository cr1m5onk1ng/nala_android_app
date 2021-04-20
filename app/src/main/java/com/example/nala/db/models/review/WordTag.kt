package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.nala.db.models.DatabaseModel

@Entity(tableName = "word_tag", primaryKeys = ["word", "tag"])
data class WordTag (
    @ColumnInfo(name = "word")
    val word: String,

    @ColumnInfo(name = "tag")
    val tag: String,
) : DatabaseModel()