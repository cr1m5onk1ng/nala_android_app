package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.nala.db.converters.TimeConverter
import java.time.Instant
import java.util.*

@Entity(tableName = "articles")
data class ArticlesCache (
    @ColumnInfo(name = "url")
    @PrimaryKey
    val url: String,

    @ColumnInfo(name="title")
    val title: String,

    @ColumnInfo(name="description")
    val description: String? = null,

    @ColumnInfo(name="thumbnail_url")
    val thumbnailUrl: String? = null,

    @ColumnInfo(name = "timeAdded")
    @TypeConverters(TimeConverter::class)
    val timeAdded: Date = Date(),

    ) {
    companion object {
        fun Empty() : ArticlesCache{
            return ArticlesCache(
                url = "",
                title = ""
            )
        }
    }
}