package com.example.nala.repository

import com.example.nala.domain.model.yt.*
import kotlinx.coroutines.flow.Flow

interface YouTubeRepository {

    suspend fun getVideoComments(videoId: String) : YoutubeCommentsList

    suspend fun getVideoData(videoId: String) : YoutubeVideoModel

    suspend fun getVideoCaptions(videoId: String, lang: String = "ja") : List<YoutubeCaptionModel>

    suspend fun getVideoCaptionsTracks(videoId: String) : List<YoutubeCaptionTracksModel>

    suspend fun addVideoToFavorites(video: YoutubeVideoModel)

    suspend fun removeVideoFromFavorites(videoId: String)

    fun getSavedVideo(videoId: String) : Flow<YoutubeVideoModel>

    fun getSavedVideos() : Flow<List<YoutubeVideoModel>>

    fun getCachedVideoComments(videoId: String) : Flow<List<YoutubeCommentModel>>
}