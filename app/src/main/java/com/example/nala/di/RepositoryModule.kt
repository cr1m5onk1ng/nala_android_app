package com.example.nala.di

import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.WordReviewDbDtoMapper
import com.example.nala.network.model.dictionary.DictionaryModelDtoMapper
import com.example.nala.network.model.kanji.KanjiCollectionDtoMapper
import com.example.nala.network.model.kanji.StoriesCollectionDtoMapper
import com.example.nala.network.services.DictionaryService
import com.example.nala.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDictionaryRepository(
        dictionaryService : DictionaryService,
        networkMapper : DictionaryModelDtoMapper,
    ) : DictionaryRepository {
        return DictionaryRepositoryImpl(
            dictionaryService,
            networkMapper
        )
    }

    @Singleton
    @Provides
    fun provideKanjiRepository(
        kanjiMapper: KanjiCollectionDtoMapper,
        storiesMapper: StoriesCollectionDtoMapper
    ) : KanjiRepository {
        return KanjiRepositoryImpl(
            kanjiMapper,
            storiesMapper
        )
    }

    @Singleton
    @Provides
    fun provideReviewRepository(
        reviewDao: ReviewDao,
        dbMapper: WordReviewDbDtoMapper
    ) : ReviewRepository {
        return ReviewRepositoryImpl(
            reviewDao,
            dbMapper,
        )
    }

}