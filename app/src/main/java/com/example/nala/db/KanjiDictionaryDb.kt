package com.example.nala.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nala.db.dao.KanjiDictDao
import com.example.nala.db.models.kanji.*

@Database(entities = [
    KanjiDictDbModel::class,
    KanjiMeanings::class,
    KanjiOnReadings::class,
    KanjiKunReadings::class,
    KanjiStories::class,
],
    version=1)
abstract class KanjiDictionaryDb : RoomDatabase() {
    abstract fun kanjiDao() : KanjiDictDao

    companion object {
        val DATABASE_NAME: String = "kanji_dictionary_database"
    }
}