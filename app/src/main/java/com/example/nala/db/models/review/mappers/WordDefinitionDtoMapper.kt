package com.example.nala.db.models.review.mappers

import com.example.nala.db.models.review.WordDefinition
import com.example.nala.db.models.review.relations.WordWithDefinitions
import com.example.nala.domain.util.DomainMapper

class WordDefinitionDtoMapper : DomainMapper<WordDefinition, String> {
    override fun mapToDomainModel(model: WordDefinition): String {
        return model.definition
    }

    fun mapDefinitionsToDomainModel(definitions: WordWithDefinitions): List<String> {
        val definitionsModels = definitions.definitions
        return definitionsModels.map{it.definition}
    }

    override fun mapFromDomainModel(domainModel: String): WordDefinition {
        TODO("Not yet implemented")
    }

}