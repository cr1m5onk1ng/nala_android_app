package com.example.nala.network.model.yt.videos


import com.google.gson.annotations.SerializedName

data class YoutubeVideoDto(
    @SerializedName("etag")
    val etag: String?,
    @SerializedName("items")
    val items: List<Item>?,
    @SerializedName("kind")
    val kind: String?,
    @SerializedName("pageInfo")
    val pageInfo: PageInfo?
)