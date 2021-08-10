package com.example.nala.network.services

import com.example.nala.network.model.yt.comments.YoutubeVideoCommentsData
import com.example.nala.network.model.yt.videos.YoutubeVideoDto
import com.example.nala.utils.NetworkConstants
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("commentThreads")
    suspend fun getVideoComments(
        @Query("part") part: String = "snippet",
        @Query("key") key: String = NetworkConstants.YT_API_KEY,
        @Query("videoId") videoId: String,
        @Query("maxResults") maxResults: Int? = null,
        @Query("pageToken") pageToken: Int? = null,
        @Query("textFormat") textFormat: String = "plainText"
    ) : YoutubeVideoCommentsData

    @GET("videos")
    suspend fun getVideoMetaData(
        @Query("part") part: String = "snippet",
        @Query("key") key: String = NetworkConstants.YT_API_KEY,
        @Query("id") videoId: String
    ) : YoutubeVideoDto
}