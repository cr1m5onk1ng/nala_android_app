package com.example.nala.db.models.review

import com.example.nala.domain.util.DomainMapper

class WordTagDtoMapper : DomainMapper<WordTagDto, String> {
    override fun mapToDomainModel(model: WordTagDto): String {
        return model.tag
    }

    fun mapTagsToDomainModel(tags: WordAndTag): List<String> {
        val tags = tags.tags
        return tags.map{it.tag}
    }

    override fun mapFromDomainModel(domainModel: String): WordTagDto {
        TODO("Not yet implemented")
    }
}