package com.example.nala.network.model.dictionary

import com.google.gson.annotations.SerializedName

data class DataDto (
    @SerializedName("attribution")
    val attribution: AttributionDto,
    @SerializedName("is_common")
    val isCommon: Boolean,
    @SerializedName("japanese")
    val japanese: List<JapaneseDto>,
    @SerializedName("jlpt")
    val jlpt: List<String>,
    @SerializedName("senses")
    val senses: List<SenseDto>,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("tags")
    val tags: List<String>)