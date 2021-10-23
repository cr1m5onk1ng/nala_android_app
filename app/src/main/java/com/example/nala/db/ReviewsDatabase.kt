package com.example.nala.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nala.db.converters.TimeConverter
import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.*

@Database(entities = [
    WordReviewModel::class,
    WordReviewModelFts::class,
    WordDefinition::class,
    WordTag::class,
    WordSenseDb::class,
    WordSenseTagDb::class,
    WordSenseDefinitionDb::class,
    SentenceReviewCache::class,
    SentenceReviewCacheFts::class,
    KanjiReviewCache::class,
    KanjiMeanings::class,
    KanjiOn::class,
    KanjiKun::class,
    ArticlesCache::class],
    version=8)
@TypeConverters(TimeConverter::class)
abstract class ReviewDatabase : RoomDatabase() {
    abstract  fun reviewDao() : ReviewDao

    companion object {
        val DATABASE_NAME: String = "review_database"
    }
}