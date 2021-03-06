package com.example.nala.db.models.yt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName="video_comments_cache",
    foreignKeys = [
        ForeignKey(
            entity = YoutubeCommentsCache::class,
            parentColumns = arrayOf("comment_id"),
            childColumns = arrayOf("parent_comment_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class YoutubeCommentsCache(
    @PrimaryKey
    @ColumnInfo(name="comment_id")
    val commentId: String,
    @ColumnInfo(name="parent_comment_id")
    val parentCommentId: String? = null,
    @ColumnInfo(name="video_id")
    val videoId: String,
    @ColumnInfo(name="page_id")
    val pageId: String? = null,
    @ColumnInfo(name="comment")
    val comment: String,
    @ColumnInfo(name="author")
    val author: String,
    @ColumnInfo(name="profile_img_url")
    val profileImageUrl: String? = null,
    @ColumnInfo(name="likes_count")
    val likesCount: Int? = null,
    @ColumnInfo(name="dislike_count")
    val dislikesCount: Int? = null,
    @ColumnInfo(name="published_at")
    val publishedAt: String? = null,
)