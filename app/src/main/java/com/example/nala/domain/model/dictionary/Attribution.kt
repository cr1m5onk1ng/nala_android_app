package com.example.nala.domain.model.dictionary

import com.example.nala.domain.model.DomainModel

data class Attribution(
    val dbpedia: Any? = null,
    val jmdict: Boolean? = null,
    val jmnedict: Boolean? = null,
) : DomainModel()