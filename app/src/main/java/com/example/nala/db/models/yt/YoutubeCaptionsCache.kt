package com.example.nala.db.models.yt

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName="video_captions_cache",
    primaryKeys = ["video_id", "lang", "start"],
)
data class YoutubeCaptionsCache(
    @ColumnInfo(name="video_id")
    val videoId: String,
    @ColumnInfo(name="lang")
    val lang: String,
    @ColumnInfo(name="start")
    val start: Float,
    @ColumnInfo(name="duration")
    val duration: Float,
    @ColumnInfo(name="caption")
    val caption: String,
)