package com.example.nala.network.model.kanji

import com.example.nala.domain.model.kanji.Reading
import com.google.gson.annotations.SerializedName

data class KanjiModelDto (
    @SerializedName("freq")
    val freq: String?,
    @SerializedName("grade")
    val grade: String?,
    @SerializedName("jlpt")
    val jlpt: String?,
    @SerializedName("kanji")
    val kanji: String,
    @SerializedName("meaning")
    val meaning: List<String>?,
    @SerializedName("name_reading")
    val name_reading: List<String>?,
    @SerializedName("reading")
    val reading: Reading?,
    @SerializedName("strokes")
    val strokes: String?
        )