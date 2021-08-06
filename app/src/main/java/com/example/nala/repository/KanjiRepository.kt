package com.example.nala.repository

import android.content.Context
import com.example.nala.db.models.kanji.KanjiDictDbModel
import com.example.nala.domain.model.kanji.KanjiCollection
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.kanji.StoriesCollection
import java.io.IOException

interface KanjiRepository {
    fun loadKanjiFile(context: Context): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open("kanjidic.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString

    }

    fun loadStoriesFile(context: Context) : String? {
        val jsonString: String
        try {
            jsonString = context.assets.open("kanjistories.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    suspend fun getKanjiDict (context: Context): KanjiCollection

    suspend fun getKanjiStories(context: Context): StoriesCollection

    suspend fun getKanjiModel(kanji: String) : KanjiModel

    suspend fun getKanjiStory(kanji: String) : String

    suspend fun updateKanjiStory(kanji: String, story: String)

    suspend fun getKanjis() : List<KanjiDictDbModel>

    suspend fun getWordKanjis(word: String) : List<KanjiModel>

    suspend fun populateKanjiDatabase(
        kanjiCollection: KanjiCollection,
        storiesCollection: StoriesCollection)
}