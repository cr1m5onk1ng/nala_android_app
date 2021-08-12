package com.example.nala.db.models.yt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName="video_comments_cache",
    primaryKeys = ["video_id", "comment_id", "page"],
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
data class YoutubeCommentsCache(
    @ColumnInfo(name="video_id")
    val videoId: String,
    @ColumnInfo(name="comment_id")
    val commentId: String,
    @ColumnInfo(name="page")
    val page: Int,
    @ColumnInfo(name="comment")
    val comment: String,
    @ColumnInfo(name="author")
    val author: String,
    @ColumnInfo(name="published_at")
    val publishedAt: String,
)