package com.example.nala.db.dao

import androidx.room.*
import com.example.nala.db.models.review.*


@Dao
interface ReviewDao {

    @Query("SELECT * FROM word_review WHERE word=:word")
    suspend fun getReview(word: String) : List<WordReviewModelDto>

    @Query("SELECT * FROM word_review")
    suspend fun getAllReviews() : List<WordReviewModelDto>

    @Query("SELECT * FROM word_review LIMIT :n")
    suspend fun getNReviews(n: Int) : List<WordReviewModelDto>

    @Transaction
    @Query("SELECT * FROM word_review WHERE word=:word")
    suspend fun getWordDefinitions(word: String) : List<WordAndDefinition>

    @Transaction
    @Query("SELECT * FROM word_review WHERE word=:word")
    suspend fun getWordTags(word: String) : List<WordAndTag>

    @Query("DELETE FROM word_review")
    suspend fun deleteAllReviews()

    @Query("DELETE FROM word_definition")
    suspend fun deleteAllDefinitions()

    @Query("DELETE FROM word_tag")
    suspend fun deleteAllTags()

    @Query("SELECT * FROM word_review WHERE scheduled_date = :date")
    suspend fun getReviewsAtScheduledDate(date: String) : List<WordReviewModelDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReview(word: WordReviewModelDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTag(tag: WordTagDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDefinition(definition: WordDefinitionDto)

    @Delete
    suspend fun deleteReview(word: WordReviewModelDto)

    @Delete
    suspend fun deleteTag(tag: WordTagDto)

    @Delete
    suspend fun deleteDefinition(definition: WordDefinitionDto)

    @Update
    suspend fun updateReviewItem(wordModel: WordReviewModelDto)

}