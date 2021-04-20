package com.example.nala.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.*

@Database(entities = [
    WordReviewModel::class,
    WordDefinition::class,
    WordTag::class,
    WordSenseDb::class,
    WordSenseTagDb::class,
    WordSenseDefinitionDb::class,
    SentenceReviewModelDto::class,
    KanjiReviewModel::class,
    KanjiMeanings::class,
    KanjiOn::class,
    KanjiKun::class],
    version=6)
abstract class ReviewDatabase : RoomDatabase() {
    abstract  fun reviewDao() : ReviewDao

    companion object {
        val DATABASE_NAME: String = "review_database"
    }
}