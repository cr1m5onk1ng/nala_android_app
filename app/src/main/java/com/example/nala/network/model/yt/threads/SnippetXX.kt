package com.example.nala.network.model.yt.threads


import com.google.gson.annotations.SerializedName

data class SnippetXX(
    @SerializedName("authorChannelId")
    val authorChannelId: AuthorChannelIdX?,
    @SerializedName("authorChannelUrl")
    val authorChannelUrl: String?,
    @SerializedName("authorDisplayName")
    val authorDisplayName: String?,
    @SerializedName("authorProfileImageUrl")
    val authorProfileImageUrl: String?,
    @SerializedName("canRate")
    val canRate: Boolean?,
    @SerializedName("likeCount")
    val likeCount: Int?,
    @SerializedName("publishedAt")
    val publishedAt: String?,
    @SerializedName("textDisplay")
    val textDisplay: String?,
    @SerializedName("textOriginal")
    val textOriginal: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("videoId")
    val videoId: String?,
    @SerializedName("viewerRating")
    val viewerRating: String?
)