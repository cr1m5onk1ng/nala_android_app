package com.example.nala.domain.model.yt

data class YoutubeReplyModel(
    val videoId: String,
    val commentId: String,
    val content: String,
    val page: Int? = null,
    val publishedAt: String,
    val authorName: String,
    val authorProfileImageUrl: String? = null,
    val likeCount: Int? = null,
    val dislikesCount: Int? = null,
    val replies: List<YoutubeCommentModel> = listOf()
){
    override fun equals(other: Any?) =
        other is YoutubeCommentModel &&
                other.commentId == this.commentId

    override fun hashCode(): Int {
        return commentId.hashCode()
    }
}