package com.example.nala.network.model.kanji

import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.util.DomainMapper

class KanjiModelDtoMapper() : DomainMapper<KanjiModelDto, KanjiModel> {
    override fun mapToDomainModel(model: KanjiModelDto): KanjiModel {
        return KanjiModel(
            freq = model.freq,
            grade = model.grade,
            jlpt = model.jlpt,
            kanji = model.kanji,
            meaning = model.meaning,
            nameReading = model.name_reading,
            kunReadings = model.reading?.kunReadings ?: listOf(),
            onReadings = model.reading?.onReadings ?: listOf(),
            strokes = model.strokes
        )
    }

    override fun mapFromDomainModel(domainModel: KanjiModel): KanjiModelDto {
        TODO("Not yet implemented")
    }

}