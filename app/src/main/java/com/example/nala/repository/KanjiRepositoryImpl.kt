package com.example.nala.repository

import android.content.Context
import com.example.nala.domain.model.kanji.KanjiCollection
import com.example.nala.domain.model.kanji.StoriesCollection
import com.example.nala.network.model.kanji.KanjiCollectionDto
import com.example.nala.network.model.kanji.KanjiCollectionDtoMapper
import com.example.nala.network.model.kanji.StoriesCollectionDto
import com.example.nala.network.model.kanji.StoriesCollectionDtoMapper
import com.google.gson.Gson


class KanjiRepositoryImpl(
    private val kanjiMapper: KanjiCollectionDtoMapper,
    private val storiesMapper: StoriesCollectionDtoMapper
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
}