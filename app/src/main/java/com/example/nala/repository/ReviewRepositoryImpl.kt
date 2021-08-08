package com.example.nala.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.*
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.dictionary.Sense
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.domain.util.SuperMemo2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.*
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewDao: ReviewDao,
) : ReviewRepository {


    override suspend fun getSentenceReviewItem(
        sentence: String,
        targetWord: String
    ): SentenceReviewModel {
        return SentenceReviewModel(
            sentence = sentence,
            targetWord = targetWord
        )
    }

    override fun getNSentenceReviewItems(n: Int): Flow<List<SentenceReviewModel>> {
        val allReviews = reviewDao.getNSentenceReviews(n)
        return allReviews.map {
            it.map{
                SentenceReviewModel(
                    sentence = it.sentence,
                    targetWord = it.targetWord
                )
            }
        }
    }

    override fun getAllSentenceReviewItems(): Flow<List<SentenceReviewModel>> {
        val allReviews =  reviewDao.getAllSentenceReviews()
        return allReviews.map {
            it.map {
                SentenceReviewModel(
                    sentence = it.sentence,
                    targetWord = it.targetWord
                )
            }
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

    override fun getNKanjiReviewItems(n: Int): Flow<List<KanjiReviewModel>> {
        return reviewDao.getNKanjiReviews(n)
    }

    override fun getAllKanjiReviewItems() : Flow<List<KanjiReviewModel>> {
        return reviewDao.getKanjiReviewsAsFlow()
    }

    override suspend fun getKanjiReviewsAsString(): List<String> {
        return reviewDao.getKanjiReviews().map{ it.kanji }
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
        addKanjiMeaningsToReview(kanjiMeanings, kanjiModel.kanji)
        addKanjiOnReadingsToReview(kanjiOnReadings, kanjiModel.kanji)
        addKanjiKunReadingsToReview(kanjiKunReadings, kanjiModel.kanji)
        addKanjiMeaningsToReview(kanjiMeanings, kanjiModel.kanji)

        reviewDao.insertKanjiReview(kanjiReviewDao)
    }

    override suspend fun addKanjiMeaningsToReview(meanings: List<String>, kanji: String) {
        val meaningsList: MutableList<KanjiMeanings> = mutableListOf()
        meanings.forEach {
            val meaningDao = KanjiMeanings(
                kanji = kanji,
                meaning = it
            )
            meaningsList.add(meaningDao)
            //reviewDao.insertKanjiMeaning(meaningDao)
        }
        reviewDao.insertKanjiMeanings(*meaningsList.toTypedArray())
    }

    override suspend fun addKanjiKunReadingsToReview(readings: List<String>, kanji: String) {
        val readingsList = mutableListOf<KanjiKun>()
        readings.forEach {
            val kunDao = KanjiKun(
                kanji = kanji,
                kunReading = it
            )
            readingsList.add(kunDao)
            //reviewDao.insertKanjiKunReading(kunDao)
        }
        reviewDao.insertKanjiKunReadings(*readingsList.toTypedArray())
    }

    override suspend fun addKanjiOnReadingsToReview(readings: List<String>, kanji: String) {
        val readingsList = mutableListOf<KanjiOn>()
        readings.forEach {
            val onDao = KanjiOn(
                kanji = kanji,
                onReading = it
            )
            readingsList.add(onDao)
            //reviewDao.insertKanjiOnReading(onDao)
        }
        reviewDao.insertKanjiOnReadings(*readingsList.toTypedArray())
    }

    override suspend fun removeKanjiReviewItem(kanjiModel: KanjiReviewModel) {
        reviewDao.deleteKanjiReview(kanjiModel)
    }

    override suspend fun removeKanjiReviewItemFromId(kanji: String) {
        reviewDao.removeKanjiReviewFromId(kanji)
    }

    override suspend fun removeWordReviewFromId(word: String) {
        reviewDao.removeWordReviewFromId(word)
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

    override suspend fun isKanjiInReview(kanji: KanjiModel): Boolean {
        val kanjiReview = reviewDao.getKanjiReview(kanji.kanji)
        return kanjiReview.isNotEmpty()
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
        addWordTagsToReview(wordModel.dataTags, wordModel.word)
        addWordSensesToReview(wordModel.senses, wordModel.word)
    }

    override suspend fun addWordTagsToReview(tags: List<String>, word: String) {
        val tagsList = mutableListOf<WordTag>()
        tags.forEach {
            val tagDto = WordTag(
                word = word,
                tag = it,
            )
            tagsList.add(tagDto)
            //reviewDao.addWordTag(tagDto)
        }
        reviewDao.addWordTags(*tagsList.toTypedArray())
    }

    override suspend fun addWordSensesToReview(senses: List<Sense>, word: String) {
        Log.d("DBDEBUG", "Senses: $senses")
        val senseList = mutableListOf<WordSenseDb>()
        senses.forEach { sense ->
            val senseId = UUID.randomUUID().toString()
            val sensesDto = WordSenseDb(
                senseId = senseId,
                word = word,
                pos = sense.partsOfSpeech?.first() ?: ""
            )
            Log.d("DBDEBUG", "Added Sense: $sensesDto")
            senseList.add(sensesDto)
            addSenseTagsToReview(sense, senseId)
            addSenseDefinitionsToReview(sense, senseId)
            //reviewDao.insertWordSense(sensesDto)
        }
        reviewDao.insertWordSenses(*senseList.toTypedArray())
    }

    override suspend fun addSenseTagsToReview(sense: Sense, senseId: String) {
        val tagsList = mutableListOf<WordSenseTagDb>()
        sense.tags?.forEach{
            val tag = WordSenseTagDb(
                senseId = senseId,
                tag = it
            )
            Log.d("DBDEBUG", "Added sense tag: $tag")
            tagsList.add(tag)
            //reviewDao.insertWordSenseTag(tag)
        }
        reviewDao.insertWordSenseTags(*tagsList.toTypedArray())
    }

    override suspend fun addSenseDefinitionsToReview(sense: Sense, senseId: String) {
        val defsList = mutableListOf<WordSenseDefinitionDb>()
        sense.englishDefinitions?.forEach{
            val def = WordSenseDefinitionDb(
                senseId = senseId,
                definition = it
            )
            Log.d("DBDEBUG", "Added sense definition: $def")
            defsList.add(def)
            //reviewDao.insertWordSenseDefinition(def)
        }
        reviewDao.insertWordSenseDefinitions(*defsList.toTypedArray())
    }

    // BUG - Reviews might be empty
    override suspend fun getWordReview(word: String): WordReviewModel? {
        val review = reviewDao.getReview(word)
        return if(review.isNotEmpty()) review.first() else null
    }

    override suspend fun getWordReviewsAsString(): List<String> {
        return reviewDao.getWordReviews().map{ it.word }
    }

    override fun getWordReviews(): Flow<List<WordReviewModel>> {
        return reviewDao.getWordReviewsAsFlow()
    }

    override fun getNWordReviews(n: Int) : Flow<List<WordReviewModel>> {
        return reviewDao.getNReviews(n)
    }

    private suspend fun getWordSenses(word: String): List<Sense> {
        val wordSenses = reviewDao.getWordSenses(word).first()
        val sensesDb = wordSenses.wordReviewSenses
        return sensesDb.map { sense ->
            val tags = reviewDao.getWordSenseTags(sense.senseId)
            val definitions = reviewDao.getWordSenseDefinitions(sense.senseId)
            Sense(
                englishDefinitions = definitions?.map{it.definition},
                tags = tags?.map{it.tag},
                partsOfSpeech = listOf(sense.pos),
            )
        }
    }

    // TODO NEED TO MAP SENSES CORRECTLY
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

    override suspend fun isWordInReview(word: DictionaryModel): Boolean {
        return reviewDao.getReview(word.word).isNotEmpty()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun addArticleToFavorites(url: String) {
        val article = Articles(
            url = url,
            timeAdded = Date.from(Instant.now())
        )
        reviewDao.addArticle(article)
    }

    private suspend fun getWordTags(word: String): List<String> {
        val wordWithTags = reviewDao.getWordTags(word).first()
        return wordWithTags.tags.map{ it.tag }
    }
}