package com.example.nala.domain.model.kanji

import com.example.nala.domain.model.DomainModel

data class KanjiCollection (
    val kanjis: Map<String, KanjiModel>,
) : DomainModel()