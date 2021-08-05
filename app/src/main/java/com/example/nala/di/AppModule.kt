package com.example.nala.di

import android.content.Context
import com.example.nala.repository.DictionaryRepository
import com.example.nala.ui.BaseApplication
import com.example.nala.ui.dictionary.DictionaryForegroundService
import com.example.nala.ui.dictionary.DictionaryWindow
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

}