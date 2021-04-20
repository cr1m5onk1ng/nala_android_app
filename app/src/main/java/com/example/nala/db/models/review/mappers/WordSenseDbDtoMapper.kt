package com.example.nala.db.models.review.mappers

import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.WordSenseDb
import com.example.nala.db.models.review.relations.WordReviewWithSenses
import com.example.nala.domain.model.dictionary.Sense
import com.example.nala.domain.util.AsyncDomainMapper
import javax.inject.Inject
/*
class WordSenseDbDtoMapper @Inject constructor(
    val reviewDao: ReviewDao
) : AsyncDomainMapper<WordSenseDb, Sense> {

    private val wordSenseTagMapper = WordSenseTagsDbDtoMapper()
    private val wordSenseDefinitionMapper = WordSenseDefinitionsDbDtoMapper()

    override suspend fun mapToDomainModel(model: WordSenseDb): Sense {
        val wordSenseWithDefinitions = reviewDao.getWordSensesWithDefinitions(model.word).first()
        val wordSenseWithTags = reviewDao.getWordSensesWithTags(model.word).first()
        val definitions = wordSenseDefinitionMapper.mapSenseDefinitionsToDomainModel(wordSenseWithDefinitions)
        val tags = wordSenseTagMapper.mapSenseTagsToDomainModel(wordSenseWithTags)
        val pos = model.pos
        return Sense(
            englishDefinitions = definitions,
            tags = tags,
            partsOfSpeech = listOf(pos),
        )
    }

    override fun mapFromDomainModel(model: Sense): WordSenseDb {
        TODO("Not yet implemented")
    }

    suspend fun mapWordSensesToDomainModel(wordSenses: WordReviewWithSenses) : List<Sense> {
        return wordSenses.wordReviewSenses.map {
            mapToDomainModel(it)
        }
    }
} */