package com.example.nala.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nala.db.KanjiDictionaryDb
import com.example.nala.db.ReviewDatabase
import com.example.nala.db.VideoDatabase
import com.example.nala.db.dao.KanjiDictDao
import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.dao.VideoDao
import com.example.nala.db.models.review.mappers.KanjiReviewDbDtoMapper
import com.example.nala.db.models.review.mappers.SentenceReviewDbDtoMapper
import com.example.nala.db.models.review.mappers.WordReviewDbDtoMapper
//import com.example.nala.db.models.review.mappers.WordSenseDbDtoMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideReviewDatabase(@ApplicationContext context: Context) : ReviewDatabase{
        return Room.databaseBuilder(
            context,
            ReviewDatabase::class.java,
            ReviewDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideReviewDao(reviewDatabase: ReviewDatabase) : ReviewDao {
        return reviewDatabase.reviewDao()
    }

    @Singleton
    @Provides
    fun provideWordReviewMapper() : WordReviewDbDtoMapper {
        return WordReviewDbDtoMapper()
    }


    @Singleton
    @Provides
    fun provideSentenceReviewMapper() : SentenceReviewDbDtoMapper {
        return SentenceReviewDbDtoMapper()
    }

    @Singleton
    @Provides
    fun provideKanjiReviewMapper(reviewDao: ReviewDao) : KanjiReviewDbDtoMapper {
        return KanjiReviewDbDtoMapper(reviewDao)
    }

    @Singleton
    @Provides
    fun provideKanjiDictionaryDb(@ApplicationContext context: Context) : KanjiDictionaryDb {
        return Room.databaseBuilder(
            context,
            KanjiDictionaryDb::class.java,
            KanjiDictionaryDb.DATABASE_NAME
        )
            .createFromAsset("databases/kanji_dictionary_database.db")
            //.fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideKanjiDao(kanjiDatabase: KanjiDictionaryDb) : KanjiDictDao {
        return kanjiDatabase.kanjiDao()
    }

    @Singleton
    @Provides
    fun provideVideoDb(@ApplicationContext context: Context) : VideoDatabase {
        return Room.databaseBuilder(
            context,
            VideoDatabase::class.java,
            VideoDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideVideoDao(videoDatabase: VideoDatabase) : VideoDao {
        return videoDatabase.videoDao()
    }


}