package com.example.nala.repository

import com.example.nala.db.dao.VideoDao
import com.example.nala.db.models.yt.YoutubeCaptionsCache
import com.example.nala.db.models.yt.YoutubeDataCache
import com.example.nala.domain.model.yt.*
import com.example.nala.network.services.YouTubeApiService
import com.example.nala.network.services.YoutubeCaptionsService
import com.example.nala.service.metadata.ExtractorService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class YoutubeRepositoryImpl @Inject constructor(
    private val youtubeCaptionsService: YoutubeCaptionsService,
    private val youTubeApiService: YouTubeApiService,
    private val videoDao: VideoDao,
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
        return youtubeCaptionsService.getVideoCaptions(videoId = videoId, lang = lang).captions?.map{
            YoutubeCaptionModel(
                caption = it.content,
                start = it.start,
                duration = it.dur,
            )
        } ?: listOf()
    }

    override suspend fun addVideoToFavorites(video: YoutubeVideoModel) {
        val mappedVideo = YoutubeDataCache(
            videoId = video.id,
            publishedAt = video.publishedAt,
            title = video.title,
            thumbnailUrl = video.thumbnailUrl,
        )
        videoDao.addVideoToFavorites(mappedVideo)
    }

    override fun getSavedVideo(videoId: String): Flow<YoutubeVideoModel> {
        return videoDao.getCachedVideoDistinctUntilChanged(videoId).mapLatest {
            if(it.isEmpty()){
                YoutubeVideoModel.Empty()
            } else {
                YoutubeVideoModel(
                    id = it.first().videoId,
                    publishedAt = it.first().publishedAt,
                    title = it.first().title,
                    thumbnailUrl = it.first().thumbnailUrl,
                )
            }
        }
    }

    override suspend fun getVideoCaptionsTracks(videoId: String): List<YoutubeCaptionTracksModel> {
        return youtubeCaptionsService.getVideoCaptionsTracks(videoId=videoId).tracks?.map{
            YoutubeCaptionTracksModel(
                id = it.id,
                name = it.name,
                langCode = it.langCode,
                langOriginal = it.langOriginal,
                langTranslated = it.langTranslated,
                langDefault = it.langDefault.toBoolean(),
            )
        } ?: listOf()
    }

    override fun getSavedVideos(): Flow<List<YoutubeVideoModel>> {
        return videoDao.getCachedVideos().mapLatest { videos ->
            videos.map{
                YoutubeVideoModel(
                    id = it.videoId,
                    publishedAt = it.publishedAt,
                    title = it.title,
                    thumbnailUrl = it.thumbnailUrl,
                )
            }
        }
    }

    override suspend fun removeVideoFromFavorites(videoId: String) {
        videoDao.removeVideoFromFavorites(videoId)
    }

    override fun getCachedVideoComments(videoId: String): Flow<List<YoutubeCommentModel>> {
        return videoDao.getVideoComments(videoId).mapLatest { comments ->
            comments.map{
                YoutubeCommentModel(
                    videoId = it.videoId,
                    commentId = it.commentId,
                    content = it.comment,
                    publishedAt = it.publishedAt ?: "",
                    authorName = it.author ?: "",
                    authorProfileImageUrl = it.profileImageUrl,
                    dislikesCount = it.dislikesCount,
                    likeCount = it.likesCount,
                )
            }
        }
    }
}