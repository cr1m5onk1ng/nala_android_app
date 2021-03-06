package com.example.nala.domain.model.yt

data class YoutubeVideoModel(
    val id: String,
    val url: String,
    val title: String? = null,
    val description: String? = null,
    val publishedAt: String? = null,
    val addedAt: Long? = null,
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

    fun isEmpty() : Boolean {
        return id.isEmpty()
    }
}