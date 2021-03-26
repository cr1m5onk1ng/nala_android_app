package com.example.nala.network.model.kanji

import com.google.gson.annotations.SerializedName

data class StoriesCollectionDto (
    @SerializedName("notes")
    val notes: List<StoryModelDto>
)