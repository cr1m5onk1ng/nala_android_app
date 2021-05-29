package com.example.nala.db.dao

import androidx.room.*
import com.example.nala.db.models.review.*
import com.example.nala.db.models.review.relations.*


@Dao
interface ReviewDao : DatabaseDao{

    // KANJI REVIEW SECTION

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKanjiReview(kanjiReview: KanjiReviewModel)

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
    suspend fun deleteKanjiReview(kanjiReview: KanjiReviewModel)

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
    suspend fun getKanjiReview(kanji: String) : List<KanjiReviewModel>

    @Query("SELECT * FROM kanji_review")
    suspend fun getAllKanjiReviews() : List<KanjiReviewModel>

    @Query("SELECT * FROM kanji_review LIMIT :n")
    suspend fun getNKanjiReviews(n: Int) : List<KanjiReviewModel>

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
    suspend fun updateKanjiReviewItem(kanjiReview: KanjiReviewModel)

    // SENTENCE REVIEW SECTION
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentenceReview(sentenceReview: SentenceReviewModelDto)

    @Delete
    suspend fun deleteSentenceReview(sentenceReview: SentenceReviewModelDto)

    @Query("SELECT * FROM sentence_review WHERE sentence=:sentence AND word=:word")
    suspend fun getSentenceReview(sentence: String, word: String) : List<SentenceReviewModelDto>

    @Query("SELECT * FROM sentence_review")
    suspend fun getAllSentenceReviews() : List<SentenceReviewModelDto>

    @Query("SELECT * FROM sentence_review LIMIT :n")
    suspend fun getNSentenceReviews(n: Int) : List<SentenceReviewModelDto>

    @Update
    suspend fun updateSentenceReviewItem(sentenceReview: SentenceReviewModelDto)

    // WORD REVIEW SECTION

    @Query("SELECT * FROM word_review WHERE word=:word")
    suspend fun getReview(word: String) : List<WordReviewModel>

    @Query("SELECT * FROM word_review")
    suspend fun getAllReviews() : List<WordReviewModel>

    @Query("SELECT * FROM word_review LIMIT :n")
    suspend fun getNReviews(n: Int) : List<WordReviewModel>

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
    suspend fun getWordSensesWithDefinitions(word: String) : List<WordReviewWithSensesAndDefinitions>

    @Transaction
    @Query("SELECT * FROM word_review WHERE word=:word")
    suspend fun getWordSensesWithTags(word: String) : List<WordReviewWithSensesAndTags>


    @Transaction
    @Query("SELECT * FROM word_sense WHERE senseId=:senseId")
    suspend fun getSenseWithDefinitionsAndTags(senseId: String) : List<WordSenseWithTagsAndDefinitions>

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

}