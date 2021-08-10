package com.example.nala.domain.model.yt

data class YoutubeVideoModel(
    val id: String,
    val publishedAt: String,
    val title: String,
    val thumbnailUrl: String,
    val channelTitle: String,
    val tags: List<String>,
)