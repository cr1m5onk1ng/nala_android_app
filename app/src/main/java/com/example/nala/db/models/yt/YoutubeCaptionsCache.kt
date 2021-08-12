package com.example.nala.db.models.yt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName="video_captions_cache",
    primaryKeys = ["video_id", "lang", "start"],
    foreignKeys = [
        ForeignKey(
            entity = YoutubeDataCache::class,
            parentColumns = arrayOf("video_id"),
            childColumns = arrayOf("video_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )],
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