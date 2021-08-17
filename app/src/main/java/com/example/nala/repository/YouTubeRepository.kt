package com.example.nala.repository

import com.example.nala.domain.model.yt.*
import kotlinx.coroutines.flow.Flow

interface YouTubeRepository {

    suspend fun cacheVideoComments(comments: List<YoutubeCommentModel>)

    suspend fun cacheVideoCaptions(videoId: String, langCode: String, captions: List<YoutubeCaptionModel>)

    suspend fun cacheVideoCaptionTracks(videoId:String, tracks: List<YoutubeCaptionTracksModel>)

    suspend fun getVideoComments(videoId: String) : YoutubeCommentsList

    suspend fun getVideoData(videoId: String) : YoutubeVideoModel

    suspend fun getVideoScrapedData(url: String) : YoutubeVideoModel

    suspend fun getVideoCaptions(videoId: String, lang: String = "ja") : List<YoutubeCaptionModel>

    suspend fun getVideoCaptionsTracks(videoId: String) : List<YoutubeCaptionTracksModel>

    suspend fun addVideoToFavorites(videoId: String, videoUrl: String)

    suspend fun removeVideoFromFavorites(videoId: String)

    fun getSavedVideo(videoId: String) : Flow<YoutubeVideoModel>

    fun getSavedVideos() : Flow<List<YoutubeVideoModel>>

    fun getCachedVideoComments(videoId: String) : Flow<List<YoutubeCommentModel>>

}