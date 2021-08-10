package com.example.nala.di

import com.example.nala.network.model.dictionary.DictionaryModelDtoMapper
import com.example.nala.network.model.kanji.KanjiCollectionDtoMapper
import com.example.nala.network.model.kanji.StoriesCollectionDtoMapper
import com.example.nala.network.services.DictionaryService
import com.example.nala.network.services.SearchApiService
import com.example.nala.network.services.YouTubeApiService
import com.example.nala.network.services.YoutubeCaptionsService
import com.example.nala.service.tokenization.JapaneseTokenizerService
import com.example.nala.utils.NetworkConstants
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
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
    fun provideTokenizerService() : JapaneseTokenizerService {
        return JapaneseTokenizerService()
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

    @Singleton
    @Provides
    fun provideYoutubeCaptionsService() : YoutubeCaptionsService {
        return Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.BODY )).build())
            .baseUrl(NetworkConstants.YT_CAPTIONS_ENTRY_POINT)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(YoutubeCaptionsService::class.java)
    }

    @Singleton
    @Provides
    fun provideYoutubeDataApiService() : YouTubeApiService {
        return Retrofit.Builder()
            .baseUrl(NetworkConstants.YT_ENTRY_POINT)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(YouTubeApiService::class.java)
    }

}