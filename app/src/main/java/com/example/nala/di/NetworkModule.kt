package com.example.nala.di

import com.example.nala.network.model.dictionary.DictionaryModelDtoMapper
import com.example.nala.network.model.kanji.KanjiCollectionDtoMapper
import com.example.nala.network.model.kanji.StoriesCollectionDtoMapper
import com.example.nala.network.services.DictionaryService
import com.example.nala.network.services.SearchApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideDictionaryMapper() : DictionaryModelDtoMapper {
        return DictionaryModelDtoMapper()
    }

    @Singleton
    @Provides
    fun provideDictionaryService() : DictionaryService {
        return Retrofit.Builder()
            .baseUrl("https://jisho.org/api/v1/search/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(DictionaryService::class.java)
    }

    @Singleton
    @Provides
    fun provideKanjiMapper() : KanjiCollectionDtoMapper {
        return KanjiCollectionDtoMapper()
    }

    @Singleton
    @Provides
    fun provideStoriesMapper() : StoriesCollectionDtoMapper {
        return StoriesCollectionDtoMapper()
    }

    @Singleton
    @Provides
    fun provideSearchApiService() : SearchApiService {
        return Retrofit.Builder()
            .baseUrl("SEARCH API BASE URL")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(SearchApiService::class.java)
    }

}