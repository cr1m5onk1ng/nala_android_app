package com.example.nala.network.model.dictionary

import com.google.gson.annotations.SerializedName


data class SenseDto (
    @SerializedName("antonyms")
    val antonyms: List<Any>,
    @SerializedName("english_definitions")
    val englishDefinitions: List<String>,
    @SerializedName("info")
    val info: List<Any>,
    @SerializedName("links")
    val links: List<LinkDto>,
    @SerializedName("parts_of_speech")
    val partsOfSpeech: List<String>,
    @SerializedName("restrictions")
    val restrictions: List<Any>,
    @SerializedName("see_also")
    val seeAlso: List<Any>,
    @SerializedName("sentences")
    val sentences: List<Any>,
    @SerializedName("source")
    val source: List<Any>,
    @SerializedName("tags")
    val tags: List<String>
    )