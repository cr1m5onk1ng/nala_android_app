package com.example.nala.repository

import android.util.Log
import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.*
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.dictionary.Sense
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.metadata.MetadataModel
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.domain.util.SuperMemo2
import com.example.nala.services.metadata.ExtractorService
import com.example.nala.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewDao: ReviewDao,
    private val metadataExtractorService: ExtractorService<MetadataModel>,
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
        return allReviews.map { sentences ->
            sentences.map{
                SentenceReviewModel(
                    sentence = it.sentence,
                    targetWord = it.targetWord
                )
            }
        }
    }

    override fun getAllSentenceReviewItems(): Flow<List<SentenceReviewModel>> {
        val allReviews =  reviewDao.getAllSentenceReviews()
        return allReviews.map { sentences ->
            sentences.map {
                SentenceReviewModel(
                    sentence = it.sentence,
                    targetWord = it.targetWord
                )
            }
        }
    }


    override suspend fun addSentenceToReview(sentenceReview: SentenceReviewModel) {
        val reviewDto = SentenceReviewCache(
            sentence = sentenceReview.sentence,
            targetWord = sentenceReview.targetWord
        )
        return reviewDao.insertSentenceReview(reviewDto)
    }

    override fun getSentenceReviewsPaged(nextPageId: Date?, limit: Int): Flow<List<SentenceReviewCache>> {

        return if(nextPageId ==  null) {
           reviewDao.getNSentenceReviews(limit)
        } else {
            reviewDao.getSentenceReviewsPaged(nextPageId, limit)
        }
    }

    override fun getKanjiReviewsPaged(nextPageId: Date?, limit: Int): Flow<List<KanjiReviewCache>> {
        return if(nextPageId == null) {
            reviewDao.getNKanjiReviews(limit)
        } else {
            reviewDao.getKanjiReviewsPaged(nextPageId, limit)
        }
    }

    override fun getWordReviewsPaged(nextPageId: Date?, limit: Int): Flow<List<WordReviewModel>> {
        if(nextPageId == null) {
            return reviewDao.getNReviews(limit)
        }
        return reviewDao.getWordReviewsPaged(nextPageId, limit)
    }

    override suspend fun removeSentenceReview(sentenceReview: SentenceReviewModel) {
        val reviewDto = SentenceReviewCache(
            sentence = sentenceReview.sentence,
            targetWord = sentenceReview.targetWord
        )
        reviewDao.deleteSentenceReview(reviewDto)
    }

    override suspend fun restoreArticle(article: ArticlesCache) {
        reviewDao.addArticleToFavorites(article)
    }

    override suspend fun updateSentenceReviewParameters(
        quality: Int,
        sentenceReview: SentenceReviewModel
    ) {
        val sr = SentenceReviewCache(
            sentence = sentenceReview.sentence,
            targetWord = sentenceReview.targetWord
        )
        val updatedParams = SuperMemo2.updateParams(
            quality = quality,
            previousRepetitions = sr.repetitions,
            previousEaseFactor = sr.easeFactor,
            previousInterval = sr.interval,
        )

        val updatedSentenceReview = SentenceReviewCache(
            sentence = sr.sentence,
            targetWord = sr.targetWord,
            repetitions = updatedParams.repetitions,
            easeFactor = updatedParams.easeFactor,
            interval = updatedParams.interval,
        )
        reviewDao.updateSentenceReviewItem(updatedSentenceReview)
    }

    override suspend fun getKanjiReviewItem(kanji: String): KanjiReviewCache {
        return reviewDao.getKanjiReview(kanji).first()
    }

    override fun getNKanjiReviewItems(n: Int): Flow<List<KanjiReviewCache>> {
        return reviewDao.getNKanjiReviews(n)
    }

    override fun getAllKanjiReviewItems() : Flow<List<KanjiReviewCache>> {
        return reviewDao.getKanjiReviewsAsFlow()
    }

    override suspend fun getKanjiReviewsAsString(): List<String> {
        return reviewDao.getKanjiReviews().map{ it.kanji }
    }

    override suspend fun addKanjiToReview(kanjiModel: KanjiModel) {
        val kanjiReviewDao = KanjiReviewCache(
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

    override suspend fun restoreKanjiToReview(kanjiReviewCache: KanjiReviewCache) {
        reviewDao.insertKanjiReview(kanjiReviewCache)
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

    override suspend fun getMatchingWords(word: String): List<WordReviewModel> {
        return reviewDao.getMatchingWords(word)
    }

    override suspend fun removeKanjiReviewItem(kanjiCache: KanjiReviewCache) {
        reviewDao.deleteKanjiReview(kanjiCache)
    }

    override suspend fun removeKanjiReviewItemFromId(kanji: String) {
        reviewDao.removeKanjiReviewFromId(kanji)
    }

    override suspend fun removeWordReviewFromId(word: String) {
        reviewDao.removeWordReviewFromId(word)
    }

    override suspend fun updateKanjiReviewParameters(
        quality: Int,
        kanjiCache: KanjiReviewCache
    ) {
        val updatedParams = SuperMemo2.updateParams(
            quality = quality,
            previousRepetitions = kanjiCache.repetitions,
            previousEaseFactor = kanjiCache.easeFactor,
            previousInterval = kanjiCache.interval,
        )

        val updatedKanjiReview = KanjiReviewCache(
            kanji = kanjiCache.kanji,
            freq = kanjiCache.freq,
            grade = kanjiCache.grade,
            jlpt = kanjiCache.jlpt,
            strokes = kanjiCache.strokes,
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

    override suspend fun getMightForgetItems(): Flow<List<WordReviewModel>> {
        return reviewDao.getMightForgetItems()
    }

    override suspend fun restoreRemovedWordToReview(wordReview: WordReviewModel) {
        reviewDao.addWordReview(wordReview)
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
            var posData = ""
            sense.partsOfSpeech?.let{
                if (it.isNotEmpty()){
                    posData = it.first()
                }
            }
            val sensesDto = WordSenseDb(
                senseId = senseId,
                word = word,
                pos = posData
            )
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
                englishDefinitions = definitions.map{it.definition},
                tags = tags.map{it.tag},
                partsOfSpeech = listOf(sense.pos),
            )
        }
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

    override suspend fun updateWordReviewParameters(quality: Int, wordModel: WordReviewModel) {
        val updatedParams = SuperMemo2.updateParams(
            quality = quality,
            previousRepetitions = wordModel.repetitions,
            previousEaseFactor = wordModel.easeFactor,
            previousInterval = wordModel.interval,
        )

        val updatedWordReview = WordReviewModel(
            word = wordModel.word,
            reading = wordModel.reading,
            jlpt = wordModel.jlpt,
            pos = wordModel.pos,
            common = wordModel.common,
            repetitions = updatedParams.repetitions,
            easeFactor = updatedParams.easeFactor,
            interval = updatedParams.interval,
        )
        reviewDao.updateWordReviewItem(updatedWordReview)
    }

    @ExperimentalCoroutinesApi
    override fun getSavedArticle(url: String): Flow<ArticlesCache> {
        return reviewDao.getSavedArticleDistinctUntilChanged(url).mapLatest{
            if(it.isEmpty()) {
                ArticlesCache.Empty()
            } else {
                it.first()
            }
        }
    }

    override suspend fun getMatchingSentences(sentence: String): List<SentenceReviewModel> {
        val sanitizedQuery = Utils.sanitizeSearchQuery(sentence)
        val matches = reviewDao.getMatchingSentences(sanitizedQuery)
        return matches.map {
            SentenceReviewModel(
                sentence = it.sentence,
                targetWord = it.targetWord,
            )
        }
    }

    override suspend fun removeWordReview(wordReview: WordReviewModel) {
        reviewDao.deleteWordReview(wordReview)
    }

    override suspend fun isWordInReview(word: DictionaryModel): Boolean {
        return reviewDao.getReview(word.word).isNotEmpty()
    }

    override suspend fun addArticleToFavorites(articleUrl: String) {
        withContext(Dispatchers.IO) {
            try{
                val metadata = metadataExtractorService.extractFromUrl(articleUrl)
                val cachedArticle = ArticlesCache(
                    url = articleUrl,
                    title = metadata.title,
                    description = metadata.description,
                    domain = Utils.parseDomainFromUrl(articleUrl),
                    thumbnailUrl = metadata.thumbnailUrl,
                )
                reviewDao.addArticleToFavorites(cachedArticle)
            } catch(e: Exception) {
                Log.d("ARTICLESDEBUG", "Something went wrong: $e")
                val cachedArticle = ArticlesCache(
                    url = articleUrl,
                    title = "No title provided",
                    description = "No description provided",
                    thumbnailUrl = null,
                )
                reviewDao.addArticleToFavorites(cachedArticle)
            }
        }
    }

    override suspend fun removeArticleFromFavorites(url: String) {
        reviewDao.removeArticleFromFavorites(url)
    }

    override fun getSavedArticles(): Flow<List<ArticlesCache>> {
        return reviewDao.getSavedArticles()
    }

    private suspend fun getWordTags(word: String): List<String> {
        val wordWithTags = reviewDao.getWordTags(word).first()
        return wordWithTags.tags.map{ it.tag }
    }
}