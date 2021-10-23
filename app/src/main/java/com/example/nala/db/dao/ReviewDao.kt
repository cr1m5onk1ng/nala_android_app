package com.example.nala.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.nala.db.models.review.*
import com.example.nala.db.models.review.relations.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*


@Dao
interface ReviewDao : DatabaseDao{

    // GENERAL REVIEW LOGIC
    fun handleWordCatAndSearch(query: String) {

    }

    // KANJI REVIEW SECTION

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKanjiReview(kanjiReview: KanjiReviewCache)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKanjiMeaning(definition: KanjiMeanings)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKanjiMeanings(vararg definitions: KanjiMeanings)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKanjiKunReading(reading: KanjiKun)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKanjiKunReadings(vararg readings: KanjiKun)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKanjiOnReading(reading: KanjiOn)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKanjiOnReadings(vararg readings: KanjiOn)

    @Delete
    suspend fun deleteKanjiReview(kanjiReview: KanjiReviewCache)

    @Delete
    suspend fun deleteKanjiDefinition(definition: KanjiMeanings)

    @Delete
    suspend fun deleteKanjiDefinitions(vararg definitions: KanjiMeanings)

    @Delete
    suspend fun deleteKanjiKunReading(reading: KanjiKun)

    @Delete
    suspend fun deleteKanjiKunReadings(vararg readings: KanjiKun)

    @Delete
    suspend fun deleteKanjiOnReading(reading: KanjiOn)

    @Delete
    suspend fun deleteKanjiOnReadings(vararg readings: KanjiOn)

    @Query("SELECT * FROM kanji_review WHERE kanji=:kanji")
    suspend fun getKanjiReview(kanji: String) : List<KanjiReviewCache>

    @Query("SELECT * FROM kanji_review ORDER BY interval, added_at")
    fun getKanjiReviewsAsFlow() : Flow<List<KanjiReviewCache>>

    @Query(
        """SELECT * FROM kanji_review 
                WHERE interval > :nextPageId 
                ORDER BY interval, added_at
                LIMIT :limit
             """
    )
    fun getKanjiReviewsPaged(nextPageId: Int, limit: Int) : Flow<List<KanjiReviewCache>>

    @Query("SELECT * FROM kanji_review")
    suspend fun getKanjiReviews() : List<KanjiReviewCache>

    @Query("SELECT * FROM kanji_review ORDER BY interval LIMIT :n")
    fun getNKanjiReviews(n: Int) : Flow<List<KanjiReviewCache>>

    @Query("DELETE FROM kanji_review WHERE kanji=:kanji")
    suspend fun removeKanjiReviewFromId(kanji: String)

    @Transaction
    @Query("SELECT * FROM kanji_review WHERE kanji=:kanji")
    suspend fun getKanjiMeanings(kanji: String) : List<KanjiWithMeanings>

    @Transaction
    @Query("SELECT * FROM kanji_review WHERE kanji=:kanji")
    suspend fun getKanjiKunReadings(kanji: String) : List<KanjiWithKunReadings>

    @Transaction
    @Query("SELECT * FROM kanji_review WHERE kanji=:kanji")
    suspend fun getKanjiOnReadings(kanji: String) : List<KanjiWithOnReadings>

    @Update
    suspend fun updateKanjiReviewItem(kanjiReview: KanjiReviewCache)

    // SENTENCE REVIEW SECTION
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentenceReview(sentenceReview: SentenceReviewCache)

    @Delete
    suspend fun deleteSentenceReview(sentenceReview: SentenceReviewCache)

    @Query("SELECT * FROM sentence_review WHERE sentence=:sentence AND word=:word")
    fun getSentenceReview(sentence: String, word: String) : Flow<List<SentenceReviewCache>>

    @Query("SELECT * FROM sentence_review ORDER BY interval, added_at")
    fun getAllSentenceReviews() : Flow<List<SentenceReviewCache>>

    @Query(
        """SELECT * FROM sentence_review 
                WHERE interval > :nextPageId 
                ORDER BY interval, added_at
                LIMIT :limit
             """
    )
    fun getSentenceReviewsPaged(nextPageId: Int, limit: Int) : Flow<List<SentenceReviewCache>>

    @Query("SELECT * FROM sentence_review ORDER BY interval LIMIT :n")
    fun getNSentenceReviews(n: Int) : Flow<List<SentenceReviewCache>>

    @Query(
        """
          SELECT * FROM sentence_review
            JOIN sentence_review_fts
            ON sentence_review.sentence = sentence_review_fts.sentence
            WHERE sentence_review_fts MATCH :query
        """
    )
    suspend fun getMatchingSentences(query: String) : List<SentenceReviewCache>

    @Update
    suspend fun updateSentenceReviewItem(sentenceReview: SentenceReviewCache)

    // WORD REVIEW SECTION

    @Query("SELECT * FROM word_review WHERE word=:word")
    suspend fun getReview(word: String) : List<WordReviewModel>

    @Query("SELECT * FROM word_review ORDER BY interval, added_at")
    fun getWordReviewsAsFlow() : Flow<List<WordReviewModel>>

    @Query(
        """SELECT * FROM word_review 
                WHERE added_at > :nextPageId 
                ORDER BY interval, added_at
                LIMIT :limit
             """
    )
    fun getWordReviewsPaged(nextPageId: Date?, limit: Int) : Flow<List<WordReviewModel>>

    @Query("""SELECT * FROM word_review
                ORDER BY interval, added_at
                LIMIT :limit
             """)
    fun getFirstReviewsPaged(limit: Int) : Flow<List<WordReviewModel>>

    @Query("SELECT * FROM word_review")
    suspend fun getWordReviews() : List<WordReviewModel>

    @Query("SELECT * FROM word_review ORDER BY interval, added_at LIMIT :n")
    fun getNReviews(n: Int) : Flow<List<WordReviewModel>>

    @Transaction
    @Query("SELECT * FROM word_review WHERE word=:word")
    suspend fun getWordSenses(word: String) : List<WordReviewWithSenses>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordSense(wordSense: WordSenseDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordSenses(vararg wordSenses: WordSenseDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordSenseTag(wordSenseTag: WordSenseTagDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordSenseTags(vararg wordSenseTags: WordSenseTagDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordSenseDefinition(wordSenseDefinition: WordSenseDefinitionDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordSenseDefinitions(vararg wordSenseDefinitions: WordSenseDefinitionDb)

    @Transaction
    @Query("SELECT * FROM word_review WHERE word=:word")
    suspend fun getWordSensesWithTags(word: String) : List<WordReviewWithSensesAndTags>

    @Transaction
    @Query("SELECT * FROM word_review WHERE word=:word")
    suspend fun getWordTags(word: String) : List<WordWithTags>

    @Query("DELETE FROM word_review")
    suspend fun deleteAllReviews()

    @Query("SELECT * FROM word_sense_tag WHERE senseId=:senseId")
    suspend fun getWordSenseTags(senseId: String) : List<WordSenseTagDb>

    @Query("SELECT * FROM word_sense_definition WHERE senseId=:senseId")
    suspend fun getWordSenseDefinitions(senseId: String) : List<WordSenseDefinitionDb>

    @Query("DELETE FROM word_definition")
    suspend fun deleteAllDefinitions()

    @Query("DELETE FROM word_tag")
    suspend fun deleteAllTags()

    @Query("DELETE FROM word_review WHERE word=:word")
    suspend fun removeWordReviewFromId(word: String)

    @Query(
        """
        SELECT * FROM word_review 
        JOIN word_review_fts
        ON word_review.word = word_review_fts.word
        WHERE word_review_fts MATCH :query
        """
    )
    suspend fun getMatchingWords(query: String) : List<WordReviewModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWordReview(word: WordReviewModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWordTag(tag: WordTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWordTags(vararg tags: WordTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWordDefinition(definition: WordDefinition)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWordDefinitions(vararg definitions: WordDefinition)

    @Delete
    suspend fun deleteWordReview(word: WordReviewModel)

    @Delete
    suspend fun deleteWordTag(tag: WordTag)

    @Delete
    suspend fun deleteWordTags(vararg tags: WordTag)

    @Delete
    suspend fun deleteWordDefinition(definition: WordDefinition)

    @Delete
    suspend fun deleteWordDefinitions(vararg definitions: WordDefinition)

    @Update
    suspend fun updateWordReviewItem(wordModel: WordReviewModel)

    // ARTICLES

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArticleToFavorites(article: ArticlesCache)

    @Query("SELECT * FROM articles WHERE url=:url")
    fun getSavedArticle(url: String) : Flow<List<ArticlesCache>>

    fun getSavedArticleDistinctUntilChanged(url: String) =
        getSavedArticle(url).distinctUntilChanged()

    @Query("SELECT * FROM articles ORDER BY timeAdded DESC")
    fun getSavedArticles() : Flow<List<ArticlesCache>>

    @Query("DELETE FROM articles WHERE url=:url")
    suspend fun removeArticleFromFavorites(url: String)

}