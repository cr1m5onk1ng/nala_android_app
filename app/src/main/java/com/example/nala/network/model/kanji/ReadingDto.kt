package com.example.nala.network.model.kanji

import com.google.gson.annotations.SerializedName

data class ReadingDto (
    @SerializedName("kun")
    val kunReadings: List<String>?,

    @SerializedName("on")
    val onReadings: List<String>?
        )