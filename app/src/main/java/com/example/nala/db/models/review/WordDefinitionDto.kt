package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.nala.db.models.DatabaseModel

@Entity(tableName = "word_definition", primaryKeys = ["word", "definition"])
data class WordDefinitionDto (
    @ColumnInfo(name = "word")
    val word: String,

    @ColumnInfo(name = "definition")
    val definition: String,
) : DatabaseModel()