package com.example.nala.network.model.kanji

import com.google.gson.annotations.SerializedName

data class StoryModelDto (
    @SerializedName("fields")
    val fields: List<String>
        )