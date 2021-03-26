package com.example.nala.di

import com.example.nala.network.services.JapaneseTokenizer
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.optimized.DefaultTermFeatures
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun provideTokenizerService(tokenizer: Tokenizer<DefaultTermFeatures>) : JapaneseTokenizer {
        return JapaneseTokenizer(tokenizer)
    }

    @Singleton
    @Provides
    fun provideTokenizer() : Tokenizer<DefaultTermFeatures> {
        return Tokenizer.createDefaultTokenizer()
    }
}