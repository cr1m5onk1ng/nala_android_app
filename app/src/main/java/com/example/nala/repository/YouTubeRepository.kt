package com.example.nala.repository

import com.example.nala.domain.model.yt.*
import kotlinx.coroutines.flow.Flow

interface YouTubeRepository {

    suspend fun getVideoComments(videoId: String, key: String?, pageId: String?) : YoutubeCommentsList

    suspend fun getVideoData(videoId: String) : YoutubeVideoModel

    suspend fun getVideoScrapedData(url: String) : YoutubeVideoModel

    suspend fun getVideoCaptions(videoId: String, lang: String = "ja") : List<YoutubeCaptionModel>

    suspend fun getVideoCaptionsTracks(videoId: String) : List<YoutubeCaptionTracksModel>

    suspend fun addVideoToFavorites(videoId: String, videoUrl: String)

    suspend fun removeVideoFromFavorites(videoUrl: String)

    suspend fun restoreVideo(video: YoutubeVideoModel)

    fun getSavedVideo(videoId: String) : Flow<YoutubeVideoModel>

    fun getSavedVideos() : Flow<List<YoutubeVideoModel>>

    fun getCachedVideoComments(videoId: String) : Flow<List<YoutubeCommentModel>>

}