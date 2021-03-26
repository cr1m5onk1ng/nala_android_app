package com.example.nala.domain.model.dictionary

import com.example.nala.domain.model.DomainModel

data class Link(
    val text: String? = null,
    val url: String? = null,
) : DomainModel()