package com.example.nala.db.models.yt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName="video_comments_cache",
    primaryKeys = ["video_id", "author", "comment"],
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
    val page: Int? = null,
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