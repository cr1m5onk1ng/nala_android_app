package com.example.nala.network.services

import com.example.nala.network.model.yt.captions.CaptionTracksList
import com.example.nala.network.model.yt.captions.CaptionsList
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeCaptionsService {

    @GET("timedtext")
    suspend fun getVideoCaptions(
        @Query("type") type: String = "track",
        @Query("v") videoId: String,
        @Query("lang") lang: String = "ja",
    ) : CaptionsList

    @GET("timedtext")
    suspend fun getVideoCaptionsTracks(
        @Query("type") type: String = "list",
        @Query("v") videoId: String,
    ) : CaptionTracksList

}