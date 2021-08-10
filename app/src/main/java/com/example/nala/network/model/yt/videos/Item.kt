package com.example.nala.network.model.yt.videos


import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("etag")
    val etag: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("kind")
    val kind: String?,
    @SerializedName("snippet")
    val snippet: Snippet?
)