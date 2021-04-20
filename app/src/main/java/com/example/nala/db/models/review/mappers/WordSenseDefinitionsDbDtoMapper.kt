package com.example.nala.db.models.review.mappers

import com.example.nala.db.models.review.WordSenseDefinitionDb
import com.example.nala.db.models.review.relations.WordSenseWithDefinitions
import com.example.nala.domain.util.DomainMapper

class WordSenseDefinitionsDbDtoMapper : DomainMapper<WordSenseDefinitionDb, String> {
    override fun mapToDomainModel(model: WordSenseDefinitionDb): String {
        return model.definition
    }

    fun mapSenseDefinitionsToDomainModel(definitions: WordSenseWithDefinitions): List<String> {
        val definitionsModels = definitions.wordSenseDefinitions
        return definitionsModels.map{it.definition}
    }

    override fun mapFromDomainModel(model: String): WordSenseDefinitionDb {
        TODO("Not yet implemented")
    }
}