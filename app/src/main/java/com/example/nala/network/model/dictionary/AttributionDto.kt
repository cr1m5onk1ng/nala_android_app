package com.example.nala.network.model.dictionary

import com.google.gson.annotations.SerializedName

data class AttributionDto (
    @SerializedName("dbpedia")
    val dbpedia: Any?,
    @SerializedName("jmdict")
    val jmdict: Boolean?,
    @SerializedName("jmnedict")
    val jmnedict: Boolean?
)