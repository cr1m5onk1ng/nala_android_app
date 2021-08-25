package com.example.nala.network.model.yt.threads


import com.google.gson.annotations.SerializedName

data class YoutubeVideoCommentsData(
    @SerializedName("etag")
    val etag: String?,
    @SerializedName("items")
    val items: List<Item>?,
    @SerializedName("kind")
    val kind: String?,
    @SerializedName("nextPageToken")
    val nextPageToken: String?,
    @SerializedName("pageInfo")
    val pageInfo: PageInfo?
)