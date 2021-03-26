package com.example.nala.repository

import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.WordReviewDbDtoMapper
import com.example.nala.db.models.review.WordReviewModelDto
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.network.model.dictionary.DictionaryModelDtoMapper
import com.example.nala.network.services.DictionaryService

class DictionaryRepositoryImpl(
    private val dictionaryService : DictionaryService,
    private val reviewDao: ReviewDao,
    private val networkMapper : DictionaryModelDtoMapper,
    private val dbMapper: WordReviewDbDtoMapper
) : DictionaryRepository {

    override suspend fun search(word: String): DictionaryModel {
        val result = dictionaryService.search(word);
        return networkMapper.mapToDomainModel(result);
    }

    override suspend fun addToReview(wordModel: DictionaryModel) {
        val word = wordModel.data?.first().slug ?: ""
        val definitions = wordModel.data
            ?.first()
            .senses
            ?.first()
            ?.englishDefinitions
            ?: listOf()

        val sensesTags = wordModel.data?.first()?.senses?.first()?.tags ?: listOf()
        val dataTags = wordModel.data?.first()?.tags ?: listOf()
        val allTags = sensesTags + dataTags
        val reviewModelDto = dbMapper.mapFromDomainModel(wordModel)
        val definitionsDto = dbMapper.mapDefinitionsFromDomainModel(
            word = word,
            definitions =definitions)
        val tagsDto = dbMapper.mapTagsFromDomainModel(
            word = word,
            tags = allTags
        )
        reviewDao.addReview(reviewModelDto)
        definitionsDto.forEach{
            reviewDao.addDefinition(it)
        }
        tagsDto.forEach{
            reviewDao.addTag(it)
        }
    }

    override suspend fun getReviewItems(): List<DictionaryModel> {
        val reviews = reviewDao.getAllReviews()
        return mapReviewsToDomain(reviews)
    }

    override suspend fun getNReviewItems(n: Int) : List<DictionaryModel> {
        val reviews = reviewDao.getNReviews(n)
        return mapReviewsToDomain(reviews)
    }

    private suspend fun mapReviewsToDomain(
        reviews: List<WordReviewModelDto>,
    ) : List<DictionaryModel> {
        var reviewList: MutableList<DictionaryModel> = mutableListOf()
        reviews.forEach{
            val wordDefinitions = reviewDao.getWordDefinitions(it.word)
            val wordTags = reviewDao.getWordTags(it.word)
            reviewList.add(
                dbMapper.mapReviewToDomainModel(
                    wordModel = it,
                    definitionsModel = wordDefinitions.first(),
                    tagsModel = wordTags.first()
                )
            )
        }
        return reviewList
    }

}