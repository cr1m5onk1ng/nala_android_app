package com.example.nala.repository

import com.example.nala.domain.model.yt.*
import com.example.nala.network.services.YouTubeApiService
import com.example.nala.network.services.YoutubeCaptionsService
import com.example.nala.service.metadata.ExtractorService
import javax.inject.Inject

class YoutubeRepositoryImpl @Inject constructor(
    private val youtubeCaptionsService: YoutubeCaptionsService,
    private val youTubeApiService: YouTubeApiService,
    ) : YouTubeRepository {

    private val commentMapper = YoutubeCommentMapper()
    private val videoMapper = YoutubeVideoMapper()

    override suspend fun getVideoComments(videoId: String): YoutubeCommentsList {
        return commentMapper.mapToDomainModel(
            youTubeApiService.getVideoComments(videoId = videoId)
        )
    }

    override suspend fun getVideoData(videoId: String): YoutubeVideoModel {
        return videoMapper.mapToDomainModel(
            youTubeApiService.getVideoMetaData(videoId = videoId)
        )
    }

    override suspend fun getVideoCaptions(
        videoId: String,
        lang: String,
    ): List<YoutubeCaptionModel> {
        return youtubeCaptionsService.getVideoCaptions(videoId = videoId, lang = lang).captions.map{
            YoutubeCaptionModel(
                caption = it.content,
                start = it.start,
                duration = it.dur,
            )
        }
    }
}