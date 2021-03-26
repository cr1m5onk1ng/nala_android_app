package com.example.nala.network.model.kanji

import com.example.nala.domain.model.kanji.StoryModel
import com.example.nala.domain.util.DomainMapper

class StoryModelDtoMapper : DomainMapper<StoryModelDto, StoryModel> {
    override fun mapToDomainModel(model: StoryModelDto): StoryModel {
        val fields = model.fields
        return StoryModel(
            kanji = fields.first(),
            story = fields[7]
        )
    }

    override fun mapFromDomainModel(domainModel: StoryModel): StoryModelDto {
        TODO("Not yet implemented")
    }
}