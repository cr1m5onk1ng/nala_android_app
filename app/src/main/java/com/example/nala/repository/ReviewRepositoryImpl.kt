package com.example.nala.repository

import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.*
import com.example.nala.db.models.review.mappers.*
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.dictionary.Sense
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.domain.util.SuperMemo2
import java.util.*
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewDao: ReviewDao,
) : ReviewRepository {

    private val tagsMapper = WordTagDtoMapper()

    override suspend fun getSentenceReviewItem(
        sentence: String,
        targetWord: String
    ): SentenceReviewModel {
        return SentenceReviewModel(
            sentence = sentence,
            targetWord = targetWord
        )
    }

    override suspend fun getNSentenceReviewItems(n: Int): List<SentenceReviewModel> {
        val allReviews = reviewDao.getNSentenceReviews(n)
        return allReviews.map {
            SentenceReviewModel(
                sentence = it.sentence,
                targetWord = it.targetWord
            )
        }
    }

    override suspend fun getAllSentenceReviewItems(): List<SentenceReviewModel> {
        val allReviews =  reviewDao.getAllSentenceReviews()
        return allReviews.map {
            SentenceReviewModel(
                sentence = it.sentence,
                targetWord = it.targetWord
            )
        }
    }

    override suspend fun addSentenceToReview(sentenceReview: SentenceReviewModel) {
        val reviewDto = SentenceReviewModelDto(
            sentence = sentenceReview.sentence,
            targetWord = sentenceReview.targetWord
        )
        return reviewDao.insertSentenceReview(reviewDto)
    }

    override suspend fun removeSentenceReview(sentenceReview: SentenceReviewModel) {
        val reviewDto = SentenceReviewModelDto(
            sentence = sentenceReview.sentence,
            targetWord = sentenceReview.targetWord
        )
        reviewDao.deleteSentenceReview(reviewDto)
    }

    override suspend fun updateSentenceReviewParameters(
        quality: Int,
        sentenceModel: SentenceReviewModel
    ) {
        val sentenceReview = SentenceReviewModelDto(
            sentence = sentenceModel.sentence,
            targetWord = sentenceModel.targetWord
        )
        val updatedParams = SuperMemo2.updateParams(
            quality = quality,
            previousRepetitions = sentenceReview.repetitions,
            previousEaseFactor = sentenceReview.easeFactor,
            previousInterval = sentenceReview.interval,
        )

        val updatedSentenceReview = SentenceReviewModelDto(
            sentence = sentenceReview.sentence,
            targetWord = sentenceReview.targetWord,
            repetitions = updatedParams.repetitions,
            easeFactor = updatedParams.easeFactor,
            interval = updatedParams.interval,
        )
        reviewDao.updateSentenceReviewItem(updatedSentenceReview)
    }

    override suspend fun getKanjiReviewItem(kanji: String): KanjiReviewModel {
        return reviewDao.getKanjiReview(kanji).first()
    }

    override suspend fun getNKanjiReviewItems(n: Int): List<KanjiReviewModel> {
        return reviewDao.getNKanjiReviews(n)
    }

    override suspend fun getAllKanjiReviewItems() : List<KanjiReviewModel> {
        return reviewDao.getAllKanjiReviews()
    }

    override suspend fun addKanjiToReview(kanjiModel: KanjiModel) {
        val kanjiReviewDao = KanjiReviewModel(
            kanji = kanjiModel.kanji,
            freq = kanjiModel.freq,
            grade = kanjiModel.grade,
            jlpt = kanjiModel.jlpt,
            strokes = kanjiModel.strokes,
        )
        val kanjiMeanings = kanjiModel.meaning ?: listOf()
        val kanjiOnReadings = kanjiModel.onReadings ?: listOf()
        val kanjiKunReadings = kanjiModel.kunReadings ?: listOf()
        kanjiMeanings.forEach {
            val meaningDto = KanjiMeanings(
                kanji = kanjiModel.kanji,
                meaning = it
            )
            reviewDao.insertKanjiMeaning(meaningDto)
        }

        kanjiOnReadings.forEach {
            val onReadingDto = KanjiOn(
                kanji = kanjiModel.kanji,
                onReading = it
            )
            reviewDao.insertKanjiOnReading(onReadingDto)
        }

        kanjiKunReadings.forEach {
            val kunReadingDto = KanjiKun(
                kanji = kanjiModel.kanji,
                kunReading = it
            )
            reviewDao.insertKanjiKunReading(kunReadingDto)
        }
        reviewDao.insertKanjiReview(kanjiReviewDao)
    }

    override suspend fun addKanjiMeaningsToReview(meanings: List<String>, kanji: String) {
        meanings.forEach {
            val meaningDao = KanjiMeanings(
                kanji = kanji,
                meaning = it
            )
            reviewDao.insertKanjiMeaning(meaningDao)
        }
    }

    override suspend fun addKanjiKunReadingsToReview(readings: List<String>, kanji: String) {
        readings.forEach {
            val kunDao = KanjiKun(
                kanji = kanji,
                kunReading = it
            )
            reviewDao.insertKanjiKunReading(kunDao)
        }
    }

    override suspend fun addKanjiOnReadingsToReview(readings: List<String>, kanji: String) {
        readings.forEach {
            val onDao = KanjiOn(
                kanji = kanji,
                onReading = it
            )
            reviewDao.insertKanjiOnReading(onDao)
        }
    }

    override suspend fun removeKanjiReviewItem(kanjiModel: KanjiReviewModel) {
        reviewDao.deleteKanjiReview(kanjiModel)
    }

    override suspend fun updateKanjiReviewParameters(
        quality: Int,
        kanjiModel: KanjiReviewModel
    ) {
        val updatedParams = SuperMemo2.updateParams(
            quality = quality,
            previousRepetitions = kanjiModel.repetitions,
            previousEaseFactor = kanjiModel.easeFactor,
            previousInterval = kanjiModel.interval,
        )

        val updatedKanjiReview = KanjiReviewModel(
            kanji = kanjiModel.kanji,
            freq = kanjiModel.freq,
            grade = kanjiModel.grade,
            jlpt = kanjiModel.jlpt,
            strokes = kanjiModel.strokes,
            repetitions = updatedParams.repetitions,
            easeFactor = updatedParams.easeFactor,
            interval = updatedParams.interval,
        )
        reviewDao.updateKanjiReviewItem(updatedKanjiReview)
    }

    override suspend fun addWordToReview(wordModel: DictionaryModel) {
        // ADD WORD REVIEW MODEL
        val wordReview = WordReviewModel(
            word = wordModel.word,
            reading = wordModel.reading,
            jlpt = wordModel.jlpt,
            pos = wordModel.pos,
            common = wordModel.common,
        )
        reviewDao.addWordReview(wordReview)
    }

    override suspend fun addWordTagsToReview(tags: List<String>, word: String) {
        tags.forEach {
            val tagDto = WordTag(
                word = word,
                tag = it,
            )
            reviewDao.addWordTag(tagDto)
        }
    }

    override suspend fun addSensesToReview(senses: List<Sense>, word: String) {
        senses.forEach {
            val sensesDto = WordSenseDb(
                senseId = UUID.randomUUID().toString(),
                word = word,
                pos = it.partsOfSpeech?.first() ?: ""
            )
            reviewDao.insertWordSense(sensesDto)
        }

    }

    override suspend fun getWordReview(word: String): WordReviewModel {
        return reviewDao.getReview(word).first()
    }

    override suspend fun getWordReviews(): List<WordReviewModel> {
        return reviewDao.getAllReviews()
    }

    override suspend fun getNWordReviews(n: Int) : List<WordReviewModel> {
        return reviewDao.getNReviews(n)
    }

    override suspend fun getWordData(wordReview: WordReviewModel): DictionaryModel {
        val senses = getWordSenses(wordReview.word)
        val tags = getWordTags(wordReview.word)
        return DictionaryModel(
            word = wordReview.word,
            reading = wordReview.reading,
            jlpt = wordReview.jlpt,
            pos = wordReview.pos,
            common = wordReview.common,
            dataTags = tags,
            senses = senses
        )
    }

    override suspend fun updateWordReviewParameters(quality: Int, word: WordReviewModel) {
        val updatedParams = SuperMemo2.updateParams(
            quality = quality,
            previousRepetitions = word.repetitions,
            previousEaseFactor = word.easeFactor,
            previousInterval = word.interval,
        )

        val updatedWordReview = WordReviewModel(
            word = word.word,
            reading = word.reading,
            jlpt = word.jlpt,
            pos = word.pos,
            common = word.common,
            repetitions = updatedParams.repetitions,
            easeFactor = updatedParams.easeFactor,
            interval = updatedParams.interval,
        )
        reviewDao.updateWordReviewItem(updatedWordReview)
    }

    override suspend fun removeWordReview(wordReview: WordReviewModel) {
        reviewDao.deleteWordReview(wordReview)
    }

    private suspend fun getWordSenses(word: String): List<Sense> {
        val wordSenseDefinitions = reviewDao.getWordSensesWithDefinitions(word).first()
        val sensesDb = wordSenseDefinitions.sensesWithDefinitions
        return sensesDb.map {
            val sense = it.wordSense
            val definitions = it.wordSenseDefinitions
            Sense(
                englishDefinitions = definitions.map{it.definition},
                partsOfSpeech = listOf(sense.pos),
            )
        }
    }

    private suspend fun getWordTags(word: String): List<String> {
        val wordWithTags = reviewDao.getWordTags(word).first()
        return wordWithTags.tags.map{ it.tag }
    }
}