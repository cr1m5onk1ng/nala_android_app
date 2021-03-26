package com.example.nala.domain.model.dictionary

import com.example.nala.domain.model.DomainModel

data class Sense(
    val antonyms: List<Any>? = null,
    val englishDefinitions: List<String>? = null,
    val info: List<Any>? = null,
    val links: List<Link>? = null,
    val partsOfSpeech: List<String>? = null,
    val restrictions: List<Any>? = null,
    val seeAlso: List<Any>? = null,
    val sentences: List<Any>? = null,
    val source: List<Any>? = null,
    val tags: List<String>? = null,
) : DomainModel()