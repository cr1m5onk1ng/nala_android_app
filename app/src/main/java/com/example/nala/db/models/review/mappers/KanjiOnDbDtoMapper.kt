package com.example.nala.db.models.review.mappers

import com.example.nala.db.models.review.KanjiOn
import com.example.nala.db.models.review.relations.KanjiWithOnReadings
import com.example.nala.domain.util.DomainMapper

class KanjiOnDbDtoMapper : DomainMapper<KanjiOn, String> {

    override fun mapToDomainModel(model: KanjiOn): String {
        return model.onReading
    }

    fun mapKunReadingsToDomainModel(readings: KanjiWithOnReadings): List<String> {
        val readingsModel = readings.onReadings
        return readingsModel.map{it.onReading}
    }

    override fun mapFromDomainModel(model: String): KanjiOn {
        TODO("Not yet implemented")
    }
}