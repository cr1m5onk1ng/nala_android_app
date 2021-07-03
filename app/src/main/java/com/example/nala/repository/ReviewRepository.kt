package com.example.nala.repository

import com.example.nala.db.models.review.Articles
import com.example.nala.db.models.review.KanjiReviewModel
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.dictionary.Sense
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.review.SentenceReviewModel

interface ReviewRepository {

    // SENTENCE REVIEWS SECTION
    suspend fun getSentenceReviewItem(sentence: String, targetWord: String) : SentenceReviewModel?

    suspend fun getNSentenceReviewItems(n: Int) : List<SentenceReviewModel>

    suspend fun getAllSentenceReviewItems() : List<SentenceReviewModel>

    suspend fun addSentenceToReview(sentenceReview: SentenceReviewModel)

    suspend fun removeSentenceReview(sentenceReview: SentenceReviewModel)

    suspend fun updateSentenceReviewParameters(quality: Int, sentenceReview: SentenceReviewModel)

    // KANJI REVIEWS SECTION
    suspend fun getKanjiReviewItem(kanji: String) : KanjiReviewModel

    suspend fun getNKanjiReviewItems(n: Int) : List<KanjiReviewModel>

    suspend fun getAllKanjiReviewItems() : List<KanjiReviewModel>

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

    suspend fun getWordReviews() : List<WordReviewModel>

    suspend fun getNWordReviews(n: Int) : List<WordReviewModel>

    suspend fun getWordData(wordReview: WordReviewModel) : DictionaryModel

    suspend fun updateWordReviewParameters(quality: Int, wordModel: WordReviewModel)

    suspend fun removeWordReview(wordReview: WordReviewModel)

    // ARTICLES

    suspend fun addArticleToFavorites(url: String)

}