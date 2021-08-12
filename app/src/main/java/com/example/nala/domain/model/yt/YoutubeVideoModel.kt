package com.example.nala.domain.model.yt

data class YoutubeVideoModel(
    val id: String,
    val title: String? = null,
    val description: String? = null,
    val publishedAt: String? = null,
    val thumbnailUrl: String? = null,
    val channelTitle: String? = null,
    val tags: List<String>? = null,
){
    companion object {
        fun Empty() : YoutubeVideoModel {
            return YoutubeVideoModel(
                "",
                ""
            )
        }
    }
}