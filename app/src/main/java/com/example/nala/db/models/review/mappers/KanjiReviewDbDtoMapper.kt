package com.example.nala.db.models.review.mappers

import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.KanjiKun
import com.example.nala.db.models.review.KanjiMeanings
import com.example.nala.db.models.review.KanjiOn
import com.example.nala.db.models.review.KanjiReviewModel
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.util.AsyncDomainMapper
import javax.inject.Inject

class KanjiReviewDbDtoMapper @Inject constructor (
    val reviewDao: ReviewDao
) : AsyncDomainMapper<KanjiReviewModel, KanjiModel> {

    private val kanjiMeaningsMapper = KanjiMeaningsDbDtoMapper()
    private val kanjiKunReadingsMapper = KanjiKunDbDtoMapper()
    private val kanjiOnReadingsMapper = KanjiOnDbDtoMapper()

    override suspend fun mapToDomainModel(model: KanjiReviewModel): KanjiModel {
        val kanjiWithDefinitions = reviewDao.getKanjiMeanings(model.kanji)
        val kanjiWithKunReadings = reviewDao.getKanjiKunReadings(model.kanji)
        val kanjiWithOnReadings = reviewDao.getKanjiOnReadings(model.kanji)
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
            freq = model.freq,
            grade = model.grade,
            jlpt = model.jlpt,
            kanji = model.kanji,
            meaning = definitions,
            nameReading = listOf(),
            kunReadings = kunReadings,
            onReadings = onReadings,
            strokes = model.strokes
        )
    }

    override fun mapFromDomainModel(model: KanjiModel): KanjiReviewModel {
        return KanjiReviewModel(
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