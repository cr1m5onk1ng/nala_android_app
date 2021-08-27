package com.example.nala.db.models.yt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName="youtube_comments_pages",
    primaryKeys = ["page_id", "comment_id"],
    foreignKeys = [
        ForeignKey(
            entity = YoutubeCommentsCache::class,
            parentColumns = arrayOf("comment_id"),
            childColumns = arrayOf("comment_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
data class YoutubeCommentsPagesCache(
    @ColumnInfo(name="page_id")
    val pageId: String,
    @ColumnInfo(name="comment_id")
    val commentId: String,
)