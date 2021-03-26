package com.example.nala.db.models.review

import com.example.nala.domain.util.DomainMapper

class WordDefinitionDtoMapper : DomainMapper<WordDefinitionDto, String> {
    override fun mapToDomainModel(model: WordDefinitionDto): String {
        return model.definition
    }

    fun mapDefinitionsToDomainModel(definitions: WordAndDefinition): List<String> {
        val definitionsModels = definitions.definitions
        return definitionsModels.map{it.definition}
    }

    override fun mapFromDomainModel(domainModel: String): WordDefinitionDto {
        TODO("Not yet implemented")
    }

}