package com.example.nala.db.models.review.mappers

import com.example.nala.db.models.review.KanjiMeanings
import com.example.nala.db.models.review.relations.KanjiWithMeanings
import com.example.nala.domain.util.DomainMapper

class KanjiMeaningsDbDtoMapper : DomainMapper<KanjiMeanings, String> {
    override fun mapToDomainModel(model: KanjiMeanings): String {
        return model.meaning
    }

    fun mapDefinitionsToDomainModel(meanings: KanjiWithMeanings): List<String> {
        val readingsModel = meanings.meanings
        return readingsModel.map{it.meaning}
    }

    override fun mapFromDomainModel(model: String): KanjiMeanings {
        TODO("Not yet implemented")
    }
}