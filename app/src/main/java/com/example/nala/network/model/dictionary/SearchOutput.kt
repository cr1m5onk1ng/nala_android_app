package com.example.nala.network.model.dictionary

import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.dictionary.Sense

data class SearchOutput (
    val dictionaryModel: DictionaryModel,
    val wordSenses: List<Sense>,
    val wordTags: List<String>
    )