package com.example.nala.network.model.kanji

import com.example.nala.domain.model.kanji.KanjiModel
import com.google.gson.annotations.SerializedName

data class KanjiCollectionDto (
    @SerializedName("words")
    val kanjis: List<KanjiModelDto>
    )