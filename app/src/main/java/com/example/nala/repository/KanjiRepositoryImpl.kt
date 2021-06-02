package com.example.nala.repository

import android.content.Context
import android.util.Log
import com.example.nala.db.dao.KanjiDictDao
import com.example.nala.db.models.kanji.KanjiDictDbModel
import com.example.nala.db.models.kanji.KanjiStories
import com.example.nala.db.models.kanji.*
import com.example.nala.domain.model.kanji.KanjiCollection
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.kanji.StoriesCollection
import com.example.nala.network.model.kanji.KanjiCollectionDto
import com.example.nala.network.model.kanji.KanjiCollectionDtoMapper
import com.example.nala.network.model.kanji.StoriesCollectionDto
import com.example.nala.network.model.kanji.StoriesCollectionDtoMapper
import com.google.gson.Gson
import javax.inject.Inject


class KanjiRepositoryImpl @Inject constructor(
    private val kanjiMapper: KanjiCollectionDtoMapper,
    private val storiesMapper: StoriesCollectionDtoMapper,
    private val kanjiDao: KanjiDictDao,
): KanjiRepository {

    override suspend fun getKanjiDict(context: Context) : KanjiCollection {
        val jsonString = loadKanjiFile(context)
        val kanjiCollectionDto = Gson().fromJson(jsonString, KanjiCollectionDto::class.java)
        return kanjiMapper.mapToDomainModel(kanjiCollectionDto)
    }

    override suspend fun getKanjiStories(context: Context): StoriesCollection {
        val jsonString = loadStoriesFile(context)
        val storiesCollectionDto = Gson().fromJson(jsonString, StoriesCollectionDto::class.java)
        return storiesMapper.mapToDomainModel(storiesCollectionDto)
    }

    override suspend fun getKanjiModel(kanji: String): KanjiModel {
        val kanjiDto = kanjiDao.getKanji(kanji)
        if (kanjiDto.isNotEmpty()) {
            val k = kanjiDto.first()
            Log.d("KANJIDEBUG", "Kanji from DB: $k")
            val kanjiMeanings = kanjiDao.getKanjiMeanings(kanji).map{it.meaning}
            val kanjiKuns = kanjiDao.getKanjiKunReadings(kanji).map{it.kunReading}
            val kanjiOns = kanjiDao.getKanjiOnReadings(kanji).map{it.onReading}
            return KanjiModel(
                freq = k.freq,
                grade = k.grade,
                jlpt = k.jlpt,
                kanji = k.kanji,
                meaning = kanjiMeanings,
                kunReadings = kanjiKuns ,
                onReadings = kanjiOns,
                strokes = k.strokes
            )
        }
        return KanjiModel.Empty()
    }

    override suspend fun getKanjiStory(kanji: String): String {
        val result = kanjiDao.getKanjiStory(kanji)
        return if(result.isNotEmpty()) result.first() else ""
    }

    override suspend fun updateKanjiStory(kanji: String, story: String) {
        val newStory = KanjiStories(
            kanji = kanji,
            story = story,
        )
        kanjiDao.updateKanjiStory(newStory)
    }


    override suspend fun getKanjis(): List<KanjiDictDbModel> {
        return kanjiDao.getKanjis()
    }

    override suspend fun populateKanjiDatabase(
        kanjiCollection: KanjiCollection,
        storiesCollection: StoriesCollection) {

        // ADD KANJI MODELS
        val kanjiModels = kanjiCollection.kanjis.values.map{
            KanjiDictDbModel(
                kanji = it.kanji,
                freq = it.freq,
                grade = it.grade,
                jlpt = it.jlpt,
                strokes = it.strokes,
            )
        }.toList()

        val storyModels = storiesCollection.stories.map{ entry ->
            KanjiStories(
                kanji = entry.key,
                story = entry.value
            )
        }.toList()

        kanjiDao.addKanjiModels(*kanjiModels.toTypedArray())
        kanjiDao.addStories(*storyModels.toTypedArray())

        // ADD MEANINGS AND READINGS
        kanjiCollection.kanjis.values.forEach{ kanji ->
            val meanings = kanji.meaning
            val meaningsDtoList = meanings?.map{ meaning ->
                KanjiMeanings(
                    kanji = kanji.kanji,
                    meaning = meaning
                )
            }
            kanjiDao.addKanjiMeanings(*meaningsDtoList?.toTypedArray() ?: arrayOf())

            val kunReadings = kanji.kunReadings
            val kunReadingsDtoList = kunReadings?.map{ kun ->
                KanjiKunReadings(
                    kanji = kanji.kanji,
                    kunReading = kun,
                )
            }
            kanjiDao.addKanjiKunReadings(*kunReadingsDtoList?.toTypedArray() ?: arrayOf())

            val onReadings = kanji.onReadings
            val onReadingsDtoList = onReadings?.map{ on ->
                KanjiOnReadings(
                    kanji = kanji.kanji,
                    onReading = on,
                )
            }
            kanjiDao.addKanjiOnReadings(*onReadingsDtoList?.toTypedArray() ?: arrayOf())
        }
    }
}