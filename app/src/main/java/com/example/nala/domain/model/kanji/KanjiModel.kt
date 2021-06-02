package com.example.nala.domain.model.kanji

import com.example.nala.domain.model.DomainModel


data class KanjiModel(
    val freq: String? = null,
    val grade: String? = null,
    val jlpt: String? = null,
    val kanji: String,
    val meaning: List<String>? = null,
    val nameReading: List<String>? = null,
    val kunReadings: List<String>? = null,
    val onReadings: List<String>? = null,
    val strokes: String? = null
) : DomainModel() {
    companion object {
        fun Empty() : KanjiModel {
            return KanjiModel(
                freq = null,
                grade = null,
                jlpt = null,
                kanji = "",
                meaning = null,
                nameReading = null,
                kunReadings = listOf(),
                onReadings = listOf(),
                strokes = null
            )
        }
    }

    fun isEmpty() : Boolean {
        return kanji == ""
    }
}