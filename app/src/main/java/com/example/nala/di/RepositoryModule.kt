package com.example.nala.di

import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.WordReviewDbDtoMapper
import com.example.nala.network.model.dictionary.DictionaryModelDtoMapper
import com.example.nala.network.model.kanji.KanjiCollectionDtoMapper
import com.example.nala.network.model.kanji.StoriesCollectionDtoMapper
import com.example.nala.network.services.DictionaryService
import com.example.nala.repository.DictionaryRepository
import com.example.nala.repository.DictionaryRepositoryImpl
import com.example.nala.repository.KanjiRepository
import com.example.nala.repository.KanjiRepositoryImpl
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
        reviewDao: ReviewDao,
         networkMapper : DictionaryModelDtoMapper,
        dbMapper: WordReviewDbDtoMapper
    ) : DictionaryRepository {
        return DictionaryRepositoryImpl(
            dictionaryService,
            reviewDao,
            networkMapper,
            dbMapper
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

}