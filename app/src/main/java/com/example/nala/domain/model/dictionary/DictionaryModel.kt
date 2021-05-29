package com.example.nala.domain.model.dictionary

import com.example.nala.domain.model.DomainModel

class DictionaryModel (
    val word: String,
    val reading: String,
    val jlpt: String? = null,
    val pos: String? = null,
    val common: Boolean? = null,
    val dataTags: List<String>,
    val senses: List<Sense>
) : DomainModel() {
    companion object {
        fun Empty() : DictionaryModel {
            return DictionaryModel(
                word = "",
                reading = "",
                dataTags = listOf(),
                senses = listOf()
            )
        }
    }

    fun isEmpty() : Boolean{
        return word.isEmpty()
    }
}