package com.example.nala.domain.model.dictionary

import com.example.nala.domain.model.DomainModel

data class DictionaryModel (
    val data: List<Data>,
    val meta: Meta? = null
) : DomainModel()