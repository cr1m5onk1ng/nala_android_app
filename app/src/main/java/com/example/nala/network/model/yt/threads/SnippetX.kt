package com.example.nala.network.model.yt.threads


import com.google.gson.annotations.SerializedName

data class SnippetX(
    @SerializedName("canReply")
    val canReply: Boolean?,
    @SerializedName("isPublic")
    val isPublic: Boolean?,
    @SerializedName("topLevelComment")
    val topLevelComment: TopLevelComment?,
    @SerializedName("totalReplyCount")
    val totalReplyCount: Int?,
    @SerializedName("videoId")
    val videoId: String?
)