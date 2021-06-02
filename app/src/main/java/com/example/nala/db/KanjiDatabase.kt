package com.example.nala.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nala.db.dao.KanjiDictDao
import com.example.nala.db.models.kanji.KanjiDictDbModel
import com.example.nala.db.models.kanji.KanjiStories
import com.example.nala.db.models.kanji.*

@Database(entities = [
    KanjiDictDbModel::class,
    KanjiMeanings::class,
    KanjiOnReadings::class,
    KanjiKunReadings::class,
    KanjiStories::class,
    ],
    version=4)
abstract class KanjiDatabase : RoomDatabase() {
    abstract fun kanjiDao() : KanjiDictDao

    companion object {
        val DATABASE_NAME: String = "kanji_database"
    }
}