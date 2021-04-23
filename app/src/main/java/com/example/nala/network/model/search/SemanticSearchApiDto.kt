package com.example.nala.network.model.search

import com.google.gson.annotations.SerializedName

data class SemanticSearchApiDto (
    @SerializedName("query")
    val query: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("results")
    val results: List<String>
        )