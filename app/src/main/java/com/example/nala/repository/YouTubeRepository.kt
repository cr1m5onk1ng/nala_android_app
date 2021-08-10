package com.example.nala.repository

import com.example.nala.domain.model.yt.YoutubeCaptionModel
import com.example.nala.domain.model.yt.YoutubeCommentsList
import com.example.nala.domain.model.yt.YoutubeVideoModel

interface YouTubeRepository {

    suspend fun getVideoComments(videoId: String) : YoutubeCommentsList

    suspend fun getVideoData(videoId: String) : YoutubeVideoModel

    suspend fun getVideoCaptions(videoId: String, lang: String = "ja") : List<YoutubeCaptionModel>
}