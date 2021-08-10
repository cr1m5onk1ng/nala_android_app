package com.example.nala.domain.model.yt

data class YoutubeCommentsList(
    val comments: List<YoutubeCommentModel>,
    val nextPageToken: String? = null,
) {
    companion object{
        fun Empty() : YoutubeCommentsList {
            return YoutubeCommentsList(
                comments = listOf(),
            )
        }
    }
}