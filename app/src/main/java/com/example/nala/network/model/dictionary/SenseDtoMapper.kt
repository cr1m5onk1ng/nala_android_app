package com.example.nala.network.model.dictionary

import com.example.nala.domain.model.dictionary.Sense
import com.example.nala.domain.util.DomainMapper

class SenseDtoMapper : DomainMapper<SenseDto, Sense> {
    override fun mapToDomainModel(model: SenseDto): Sense {
        return Sense(
            antonyms = model.antonyms,
            englishDefinitions = model.englishDefinitions,
            info = model.info,
            links = null,
            partsOfSpeech = model.partsOfSpeech,
            restrictions = model.restrictions,
            seeAlso = model.seeAlso,
            sentences = model.sentences,
            source = model.source,
            tags = model.tags,
        )
    }

    fun mapSensesToDomainModel(senses: List<SenseDto>): List<Sense> {
        return senses.map {
            mapToDomainModel(it)
        }
    }

    override fun mapFromDomainModel(domainModel: Sense): SenseDto {
        TODO("Not yet implemented")
    }
}