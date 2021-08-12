package com.example.nala.di

import com.example.nala.db.dao.KanjiDictDao
import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.dao.VideoDao
import com.example.nala.db.models.review.mappers.KanjiReviewDbDtoMapper
import com.example.nala.db.models.review.mappers.SentenceReviewDbDtoMapper
import com.example.nala.network.model.dictionary.DictionaryModelDtoMapper
import com.example.nala.network.model.kanji.KanjiCollectionDtoMapper
import com.example.nala.network.model.kanji.StoriesCollectionDtoMapper
import com.example.nala.network.services.DictionaryService
import com.example.nala.network.services.YouTubeApiService
import com.example.nala.network.services.YoutubeCaptionsService
import com.example.nala.repository.*
import com.example.nala.service.metadata.ExtractorService
import com.example.nala.service.tokenization.JapaneseTokenizerService
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
        tokenizerService: JapaneseTokenizerService,
        networkMapper : DictionaryModelDtoMapper,
    ) : DictionaryRepository {
        return DictionaryRepositoryImpl(
            dictionaryService,
            tokenizerService,
            networkMapper
        )
    }

    @Singleton
    @Provides
    fun provideKanjiRepository(
        kanjiMapper: KanjiCollectionDtoMapper,
        storiesMapper: StoriesCollectionDtoMapper,
        kanjiDao: KanjiDictDao
    ) : KanjiRepository {
        return KanjiRepositoryImpl(
            kanjiMapper,
            storiesMapper,
            kanjiDao,
        )
    }

    @Singleton
    @Provides
    fun provideReviewRepository(
        reviewDao: ReviewDao,
        metadataExtractor: ExtractorService,
        //wordReviewMapper: WordReviewDbDtoMapper,
        //wordSensesMapper: WordSenseDbDtoMapper,
        //sentenceReviewMapper: SentenceReviewDbDtoMapper,
        //kanjiReviewMapper: KanjiReviewDbDtoMapper
    ) : ReviewRepository {
        return ReviewRepositoryImpl(
            reviewDao,
            metadataExtractor,
            //wordReviewMapper,
            //wordSensesMapper,
            //sentenceReviewMapper,
            //kanjiReviewMapper
        )
    }

    @Singleton
    @Provides
    fun provideYoutubeRepository(
        youtubeCaptionsService: YoutubeCaptionsService,
        youTubeApiService: YouTubeApiService,
        videoDao: VideoDao,
    ) : YouTubeRepository {
        return YoutubeRepositoryImpl(
            youtubeCaptionsService,
            youTubeApiService,
            videoDao,
        )
    }

}