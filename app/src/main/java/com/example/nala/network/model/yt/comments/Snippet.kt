package com.example.nala.network.model.yt.comments


import com.google.gson.annotations.SerializedName

data class Snippet(
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