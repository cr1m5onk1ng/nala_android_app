package com.example.nala.network.model.yt.threads


import com.google.gson.annotations.SerializedName

data class Replies(
    @SerializedName("comments")
    val comments: List<Comment>?
)