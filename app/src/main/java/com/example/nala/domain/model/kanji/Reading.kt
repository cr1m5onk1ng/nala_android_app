package com.example.nala.domain.model.kanji

import com.example.nala.domain.model.DomainModel

data class Reading(
    val kun: List<String>?,
    val on: List<String>?
) : DomainModel()