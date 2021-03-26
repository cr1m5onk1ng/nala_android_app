package com.example.nala.network.model.dictionary

import com.google.gson.annotations.SerializedName

data class JapaneseDto (
    @SerializedName("reading")
    val reading: String,
    @SerializedName("word")
    val word: String
        )