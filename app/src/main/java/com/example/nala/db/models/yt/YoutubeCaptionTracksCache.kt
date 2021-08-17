package com.example.nala.db.models.yt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName="video_caption_tracks",
    primaryKeys = [
        "video_id",
        "track_id",
    ],
    foreignKeys = [
        ForeignKey(
            entity = YoutubeDataCache::class,
            parentColumns = arrayOf("video_id"),
            childColumns = arrayOf("video_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class YoutubeCaptionTracksCache(
    @ColumnInfo(name="video_id")
    val videoId: String,
    @ColumnInfo(name="track_id")
    val trackId: String,
    @ColumnInfo(name="name")
    val name: String? = null,
    @ColumnInfo(name="lang_code")
    val langCode: String,
    @ColumnInfo(name="lang_original")
    val langOriginal: String? = null,
    @ColumnInfo(name="lang_translated")
    val langTranslated: String? = null,
    @ColumnInfo(name="lang_default")
    val langDefault: Boolean = false,
)