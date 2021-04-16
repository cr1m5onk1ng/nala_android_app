package com.example.nala.network.model.search

import com.google.gson.annotations.SerializedName

data class SearchApiDto (
    @SerializedName("results")
    val results: List<String>
        )