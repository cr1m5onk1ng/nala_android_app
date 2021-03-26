package com.example.nala.di

import android.content.Context
import androidx.room.Room
import com.example.nala.db.ReviewDatabase
import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.WordReviewDbDtoMapper
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
        ).fallbackToDestructiveMigration().build()
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

}