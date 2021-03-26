package com.example.nala.domain.model.dictionary

import com.example.nala.domain.model.DomainModel

data class Japanese(
    val reading: String? = null,
    val word: String? = null,
) : DomainModel()