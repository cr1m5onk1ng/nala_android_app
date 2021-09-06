package com.example.nala.network.services

import com.example.nala.BuildConfig
import com.example.nala.network.model.yt.threads.YoutubeVideoCommentsData
import com.example.nala.network.model.yt.videos.YoutubeVideoDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("commentThreads")
    suspend fun getVideoTopComments(
        @Header("Authorization") accessToken: String? = null,
        @Query("part") part: String = "snippet,replies",
        @Query("key") key: String,
        @Query("videoId") videoId: String,
        @Query("maxResults") maxResults: Int? = null,
        @Query("pageToken") pageToken: String? = null,
        @Query("textFormat") textFormat: String = "plainText",
        @Query("order") order: String = "relevance",
    ) : YoutubeVideoCommentsData

    @GET("commentThreads")
    suspend fun getVideoTopCommentsWithToken(
        @Header("Authorization") accessToken: String? = null,
        @Query("part") part: String = "snippet,replies",
        @Query("videoId") videoId: String,
        @Query("maxResults") maxResults: Int? = null,
        @Query("pageToken") pageToken: String? = null,
        @Query("textFormat") textFormat: String = "plainText",
        @Query("order") order: String = "relevance",
    ) : YoutubeVideoCommentsData

    @GET("videos")
    suspend fun getVideoMetaData(
        @Query("part") part: String = "snippet",
        @Query("key") key: String = BuildConfig.YT_DATA_API_KEY,
        @Query("id") videoId: String
    ) : YoutubeVideoDto
}