package com.example.nala.db.models.review.mappers

import com.example.nala.db.models.review.WordSenseTagDb
import com.example.nala.db.models.review.relations.WordSenseWithTags
import com.example.nala.domain.util.DomainMapper

class WordSenseTagsDbDtoMapper : DomainMapper<WordSenseTagDb, String> {
    override fun mapToDomainModel(model: WordSenseTagDb): String {
        return model.tag
    }

    fun mapSenseTagsToDomainModel(tags: WordSenseWithTags): List<String> {
        val tagsModels = tags.wordSenseTags
        return tagsModels.map{it.tag}
    }

    override fun mapFromDomainModel(model: String): WordSenseTagDb {
        TODO("Not yet implemented")
    }
}