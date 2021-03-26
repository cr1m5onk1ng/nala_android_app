package com.example.nala.domain.model.dictionary

import com.example.nala.domain.model.DomainModel

data class Data(
    val attribution: Attribution? = null,
    val isCommon: Boolean? = null,
    val japanese: List<Japanese>? = null,
    val jlpt: List<String>? = null,
    val senses: List<Sense>? = null,
    val slug: String? = null,
    val tags: List<String>? = null,
) : DomainModel()