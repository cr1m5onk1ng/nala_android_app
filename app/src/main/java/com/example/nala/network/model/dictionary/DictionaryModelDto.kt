package com.example.nala.network.model.dictionary

import com.example.nala.domain.model.dictionary.Meta
import com.google.gson.annotations.SerializedName

data class DictionaryModelDto (
    @SerializedName("data")
    val data: List<DataDto>,
    @SerializedName("meta")
    val meta: Meta
        )