package com.example.nala.di

import android.content.Context
import com.example.nala.services.metadata.ExtractorService
import com.example.nala.services.metadata.MetadataExtractorService
import com.example.nala.services.tokenization.JapaneseTokenizerService
import com.example.nala.services.tokenization.TokenizerService
import com.example.nala.ui.BaseApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context) : BaseApplication {
        return app as BaseApplication
    }

    @Singleton
    @Provides
    fun provideTokenizerService() : TokenizerService {
        return JapaneseTokenizerService()
    }

    @Singleton
    @Provides
    fun provideMetadataExtractorService() : ExtractorService {
        return MetadataExtractorService()
    }

}