package com.example.nala.db.models.review.mappers

import com.example.nala.db.models.review.WordTag
import com.example.nala.db.models.review.relations.WordWithTags
import com.example.nala.domain.util.DomainMapper

class WordTagDtoMapper : DomainMapper<WordTag, String> {
    override fun mapToDomainModel(model: WordTag): String {
        return model.tag
    }

    fun mapTagsToDomainModel(tags: WordWithTags): List<String> {
        val modelTags = tags.tags
        return modelTags.map{it.tag}
    }

    override fun mapFromDomainModel(model: String): WordTag {
        TODO("Not yet implemented")
    }
}