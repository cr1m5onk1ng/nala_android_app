package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.nala.db.converters.TimeConverter
import java.util.*

@Entity(tableName = "articles")
data class Articles (
    @ColumnInfo(name = "url")
    @PrimaryKey
    val url: String,

    @ColumnInfo(name = "timeAdded")
    @TypeConverters(TimeConverter::class)
    val timeAdded: Date

    )