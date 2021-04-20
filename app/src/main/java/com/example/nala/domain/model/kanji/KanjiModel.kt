package com.example.nala.domain.model.kanji

import com.example.nala.domain.model.DomainModel


data class KanjiModel(
    val freq: String?,
    val grade: String?,
    val jlpt: String?,
    val kanji: String,
    val meaning: List<String>?,
    val name_reading: List<String>?,
    val kunReadings: List<String>,
    val onReadings: List<String>,
    val strokes: String?
) : DomainModel() {
    companion object {
        fun Empty() : KanjiModel {
            return KanjiModel(
                freq = null,
                grade = null,
                jlpt = null,
                kanji = "",
                meaning = null,
                name_reading = null,
                kunReadings = listOf(),
                onReadings = listOf(),
                strokes = null
            )
        }
    }
}