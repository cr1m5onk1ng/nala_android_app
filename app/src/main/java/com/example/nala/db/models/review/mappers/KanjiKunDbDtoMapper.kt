package com.example.nala.db.models.review.mappers

import com.example.nala.db.models.review.KanjiKun
import com.example.nala.db.models.review.relations.KanjiWithKunReadings
import com.example.nala.domain.util.DomainMapper

class KanjiKunDbDtoMapper : DomainMapper<KanjiKun, String> {
    override fun mapToDomainModel(model: KanjiKun): String {
        return model.kunReading
    }

    fun mapKunReadingsToDomainModel(readings: KanjiWithKunReadings): List<String> {
        val readingsModel = readings.kunReadings
        return readingsModel.map{it.kunReading}
    }

    override fun mapFromDomainModel(model: String): KanjiKun {
        TODO("Not yet implemented")
    }
}