package com.example.nala.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.WordReviewDbDtoMapper
import com.example.nala.db.models.review.WordReviewModelDto
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.util.SuperMemo2
import java.time.LocalDate

class ReviewRepositoryImpl(
    private val reviewDao: ReviewDao,
    private val dbMapper: WordReviewDbDtoMapper
) : ReviewRepository {
    @RequiresApi(Build.VERSION_CODES.O)
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

    override suspend fun getNReviewItems(n: Int) : List<WordReviewModelDto> {
        return reviewDao.getNReviews(n)
        //return mapReviewsToDomain(reviews)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getTodaysReviews() : List<WordReviewModelDto> {
        return reviewDao.getReviewsAtScheduledDate(LocalDate.now().toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateReviewParameters(quality: Int, wordReview: WordReviewModelDto) {
        val updatedParams = SuperMemo2.updateParams(
            quality = quality,
            previousRepetitions = wordReview.repetitions,
            previousEaseFactor = wordReview.easeFactor,
            previousInterval = wordReview.interval,
        )
        val scheduledDate = SuperMemo2.calculateNextDate(
            wordReview.scheduledDate,
            updatedParams.interval)

        val updatedWordReview = WordReviewModelDto(
            word = wordReview.word,
            reading = wordReview.reading,
            jlpt = wordReview.jlpt,
            pos = wordReview.pos,
            common = wordReview.common,
            repetitions = updatedParams.repetitions,
            easeFactor = updatedParams.easeFactor,
            interval = updatedParams.interval,
            scheduledDate = scheduledDate
        )
        reviewDao.updateReviewItem(updatedWordReview)
    }

    override suspend fun mapReviewToDomain(wordReview: WordReviewModelDto) : DictionaryModel {
        val wordDefinitions = reviewDao.getWordDefinitions(wordReview.word)
        val wordTags = reviewDao.getWordTags(wordReview.word)
        return dbMapper.mapReviewToDomainModel(
            wordModel = wordReview,
            definitionsModel = wordDefinitions.first(),
            tagsModel = wordTags.first()
        )
    }

    override suspend fun mapReviewsToDomain(
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