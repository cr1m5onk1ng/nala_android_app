package com.example.nala.domain.model.yt

data class YoutubeCommentModel(
    val videoId: String,
    val commentId: String,
    val content: String,
    val pageId: String? = null,
    val publishedAt: String,
    val authorName: String,
    val authorProfileImageUrl: String? = null,
    val likeCount: Int? = null,
    val dislikesCount: Int? = null,
    val replies: List<YoutubeCommentModel> = listOf()
) {

    companion object {
        fun Empty() : YoutubeCommentModel {
            return YoutubeCommentModel(
                videoId = "",
                commentId = "",
                content = "",
                publishedAt = "",
                authorName = ""
            )
        }
    }

    override fun equals(other: Any?) =
        other is YoutubeCommentModel &&
                other.commentId == this.commentId

    override fun hashCode(): Int {
        return commentId.hashCode()
    }

    fun isEmpty() : Boolean {
        return videoId.isEmpty() && commentId.isEmpty()
    }
}