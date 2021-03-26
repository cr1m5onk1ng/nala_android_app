package com.example.nala.network.model.kanji

import com.example.nala.domain.model.kanji.StoriesCollection
import com.example.nala.domain.util.DomainMapper

class StoriesCollectionDtoMapper: DomainMapper<StoriesCollectionDto, StoriesCollection> {
    val storyMapper = StoryModelDtoMapper()
    override fun mapToDomainModel(model: StoriesCollectionDto): StoriesCollection {
        return StoriesCollection(
            stories = mapStoriesToDomainModel(model.notes)
        )
    }

    fun mapStoriesToDomainModel(storiesList: List<StoryModelDto>) : HashMap<String, String> {
        var storiesMap: HashMap<String, String> = HashMap()
        storiesList.forEach{ story ->
            val storyModel = storyMapper.mapToDomainModel(story)
            storiesMap[storyModel.kanji] = storyModel.story
        }
        return storiesMap
    }

    override fun mapFromDomainModel(domainModel: StoriesCollection): StoriesCollectionDto {
        TODO("Not yet implemented")
    }
}
