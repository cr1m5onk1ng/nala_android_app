package com.example.nala.repository

import com.example.nala.db.models.review.Articles
import com.example.nala.db.models.review.KanjiReviewModel
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.dictionary.Sense
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.review.SentenceReviewModel
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {

    // SENTENCE REVIEWS SECTION
    suspend fun getSentenceReviewItem(sentence: String, targetWord: String) : SentenceReviewModel?

    fun getNSentenceReviewItems(n: Int) : Flow<List<SentenceReviewModel>>

    fun getAllSentenceReviewItems() : Flow<List<SentenceReviewModel>>

    suspend fun addSentenceToReview(sentenceReview: SentenceReviewModel)

    suspend fun removeSentenceReview(sentenceReview: SentenceReviewModel)

    suspend fun updateSentenceReviewParameters(quality: Int, sentenceReview: SentenceReviewModel)

    // KANJI REVIEWS SECTION
    suspend fun getKanjiReviewItem(kanji: String) : KanjiReviewModel

    fun getNKanjiReviewItems(n: Int) : Flow<List<KanjiReviewModel>>

    fun getAllKanjiReviewItems() : Flow<List<KanjiReviewModel>>

    suspend fun addKanjiToReview(kanjiModel: KanjiModel)

    suspend fun addKanjiMeaningsToReview(meanings: List<String>, kanji: String)

    suspend fun addKanjiKunReadingsToReview(readings: List<String>, kanji: String)

    suspend fun addKanjiOnReadingsToReview(readings: List<String>, kanji: String)

    suspend fun removeKanjiReviewItem(kanjiModel: KanjiReviewModel)

    suspend fun updateKanjiReviewParameters(quality: Int, kanjiModel: KanjiReviewModel)

    // WORD REVIEWS SECTION
    suspend fun addWordToReview(wordModel: DictionaryModel)

    suspend fun addWordTagsToReview(tags: List<String>, word: String)

    suspend fun addWordSensesToReview(senses: List<Sense>, word: String)

    suspend fun addSenseTagsToReview(sense: Sense, senseId: String)

    suspend fun addSenseDefinitionsToReview(sense: Sense, senseId: String)

    suspend fun getWordReview(word: String) : WordReviewModel?

    fun getWordReviews() : Flow<List<WordReviewModel>>

    fun getNWordReviews(n: Int) : Flow<List<WordReviewModel>>

    suspend fun getWordData(wordReview: WordReviewModel) : DictionaryModel

    suspend fun updateWordReviewParameters(quality: Int, wordModel: WordReviewModel)

    suspend fun removeWordReview(wordReview: WordReviewModel)

    // ARTICLES

    suspend fun addArticleToFavorites(url: String)

}