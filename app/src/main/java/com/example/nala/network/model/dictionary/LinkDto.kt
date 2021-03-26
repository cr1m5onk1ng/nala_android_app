package com.example.nala.network.model.dictionary

import com.google.gson.annotations.SerializedName

data class LinkDto(
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("url")
    val url: String? = null,
)