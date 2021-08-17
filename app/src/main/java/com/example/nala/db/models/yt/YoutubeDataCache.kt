package com.example.nala.db.models.yt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.nala.db.converters.TimeConverter
import java.util.*

@Entity(tableName = "videos_cache")
data class YoutubeDataCache (
    @ColumnInfo(name="video_id")
    @PrimaryKey
    val videoId: String,
    @ColumnInfo(name="video_url")
    val videoUrl: String,
    @ColumnInfo(name="published_at")
    val publishedAt: String? = null,
    @ColumnInfo(name="title")
    val title: String? = null,
    @ColumnInfo(name="description")
    val description: String? = null,
    @ColumnInfo(name="thumbnail_url")
    val thumbnailUrl: String? = null,
    @ColumnInfo(name="channel_title")
    val channelTitle: String? = null,
    @ColumnInfo(name = "time_added")
    @TypeConverters(TimeConverter::class)
    val timeAdded: Date = Date()
)