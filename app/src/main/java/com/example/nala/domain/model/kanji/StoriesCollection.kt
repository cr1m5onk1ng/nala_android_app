package com.example.nala.domain.model.kanji

import com.example.nala.domain.model.DomainModel

data class StoriesCollection(
    val stories: HashMap<String, String>,
) : DomainModel()