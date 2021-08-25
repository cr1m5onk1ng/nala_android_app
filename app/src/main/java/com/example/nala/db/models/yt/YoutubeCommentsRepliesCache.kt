package com.example.nala.db.models.yt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName="comments_replies_cache",
    primaryKeys = ["parent_comment_id", "child_comment_id"],
    foreignKeys = [
        ForeignKey(
            entity = YoutubeCommentsCache::class,
            parentColumns = arrayOf("comment_id"),
            childColumns = arrayOf("parent_comment_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = YoutubeCommentsCache::class,
            parentColumns = arrayOf("comment_id"),
            childColumns = arrayOf("child_comment_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class YoutubeCommentsRepliesCache(
    @ColumnInfo(name="parent_comment_id")
    val parentCommentId: String,
    @ColumnInfo(name="child_comment_id")
    val childCommentId: String
)