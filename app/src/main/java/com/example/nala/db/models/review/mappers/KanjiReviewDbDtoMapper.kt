package com.example.nala.db.models.review.mappers

import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.KanjiKun
import com.example.nala.db.models.review.KanjiMeanings
import com.example.nala.db.models.review.KanjiOn
import com.example.nala.db.models.review.KanjiReviewCache
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.util.AsyncDomainMapper
import javax.inject.Inject

class KanjiReviewDbDtoMapper @Inject constructor (
    val reviewDao: ReviewDao
) : AsyncDomainMapper<KanjiReviewCache, KanjiModel> {

    private val kanjiMeaningsMapper = KanjiMeaningsDbDtoMapper()
    private val kanjiKunReadingsMapper = KanjiKunDbDtoMapper()
    private val kanjiOnReadingsMapper = KanjiOnDbDtoMapper()

    override suspend fun mapToDomainModel(cache: KanjiReviewCache): KanjiModel {
        val kanjiWithDefinitions = reviewDao.getKanjiMeanings(cache.kanji)
        val kanjiWithKunReadings = reviewDao.getKanjiKunReadings(cache.kanji)
        val kanjiWithOnReadings = reviewDao.getKanjiOnReadings(cache.kanji)
        val definitions = kanjiMeaningsMapper.mapDefinitionsToDomainModel(
            meanings = kanjiWithDefinitions.first(),
        )
        val kunReadings = kanjiKunReadingsMapper.mapKunReadingsToDomainModel(
            readings = kanjiWithKunReadings.first()
        )
        val onReadings = kanjiOnReadingsMapper.mapKunReadingsToDomainModel(
            readings = kanjiWithOnReadings.first()
        )
        return KanjiModel(
            freq = cache.freq,
            grade = cache.grade,
            jlpt = cache.jlpt,
            kanji = cache.kanji,
            meaning = definitions,
            nameReading = listOf(),
            kunReadings = kunReadings,
            onReadings = onReadings,
            strokes = cache.strokes
        )
    }

    override fun mapFromDomainModel(model: KanjiModel): KanjiReviewCache {
        return KanjiReviewCache(
            kanji = model.kanji,
            freq = model.freq,
            grade = model.grade,
            jlpt = model.jlpt,
            strokes = model.strokes
        )
    }

    fun mapMeaningToDto(meaning: String) : KanjiMeanings {
        return kanjiMeaningsMapper.mapFromDomainModel(meaning)
    }

    fun mapKunReadingToDto(meaning: String) : KanjiKun {
        return kanjiKunReadingsMapper.mapFromDomainModel(meaning)
    }

    fun mapOnReadingToDto(meaning: String) : KanjiOn {
        return kanjiOnReadingsMapper.mapFromDomainModel(meaning)
    }

}