package com.example.nala.db.dao

import androidx.room.*
import com.example.nala.db.models.kanji.*

@Dao
interface KanjiDictDao : DatabaseDao {

    // KANJI OPERATIONS
    @Insert
    suspend fun addKanjiModels(vararg kanjis : KanjiDictDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addKanjiMeanings(vararg meanings: KanjiMeanings)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addKanjiKunReadings(vararg readings: KanjiKunReadings)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addKanjiOnReadings(vararg readings: KanjiOnReadings)

    @Query("SELECT * FROM kanji_meanings WHERE kanji=:kanji")
    suspend fun getKanjiMeanings(kanji: String) : List<KanjiMeanings>

    @Query("SELECT * FROM kanji_kun_readings WHERE kanji=:kanji")
    suspend fun getKanjiKunReadings(kanji: String) : List<KanjiKunReadings>

    @Query("SELECT * FROM kanji_on_readings WHERE kanji=:kanji")
    suspend fun getKanjiOnReadings(kanji: String) : List<KanjiOnReadings>

    @Query("SELECT * FROM kanji_dict WHERE kanji=:kanji")
    suspend fun getKanji(kanji: String) : List<KanjiDictDbModel>

    @Query("SELECT * FROM kanji_dict")
    suspend fun getKanjis() : List<KanjiDictDbModel>

    @Query("SELECT COUNT(*) FROM kanji_dict")
    suspend fun getNumKanjis() : Int

    @Query("SELECT * from kanji_dict WHERE grade=:grade")
    suspend fun getKanjiByGrade(grade: String) : List<KanjiDictDbModel>

    @Query("SELECT * FROM kanji_dict WHERE jlpt=:jlpt")
    suspend fun getKanjiByJlptLevel(jlpt: String) : List<KanjiDictDbModel>

    @Update
    suspend fun updateKanji(kanji: KanjiDictDbModel)

    // STORIES OPERATIONS

    @Insert
    suspend fun addStories(vararg stories: KanjiStories)

    @Query("SELECT story FROM kanji_stories WHERE kanji=:kanji")
    suspend fun getKanjiStory(kanji: String) : List<String>

    @Update
    suspend fun updateKanjiStory(story: KanjiStories)


}