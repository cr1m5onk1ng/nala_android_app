package com.example.nala.repository

import com.example.nala.db.dao.VideoDao
import com.example.nala.db.models.yt.YoutubeCaptionTracksCache
import com.example.nala.db.models.yt.YoutubeCaptionsCache
import com.example.nala.db.models.yt.YoutubeCommentsCache
import com.example.nala.db.models.yt.YoutubeDataCache
import com.example.nala.domain.model.yt.*
import com.example.nala.network.model.yt.captions.CaptionsList
import com.example.nala.network.services.YouTubeApiService
import com.example.nala.network.services.YoutubeCaptionsService
import com.example.nala.services.metadata.ExtractorService
import com.example.nala.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class YoutubeRepositoryImpl @Inject constructor(
    private val youtubeCaptionsService: YoutubeCaptionsService,
    private val youTubeApiService: YouTubeApiService,
    private val videoDao: VideoDao,
    private val metadataExtractor: ExtractorService,
    ) : YouTubeRepository {

    private val commentMapper = YoutubeCommentMapper()
    private val videoMapper = YoutubeVideoMapper()

    override suspend fun getVideoComments(videoId: String): YoutubeCommentsList {
        return commentMapper.mapToDomainModel(
            youTubeApiService.getVideoComments(videoId = videoId)
        )
    }


    override suspend fun getVideoData(videoId: String): YoutubeVideoModel {
        val cachedData = videoDao.getCachedVideoData(videoId)
        if(cachedData.isEmpty()) {
            return videoMapper.mapToDomainModel(
                youTubeApiService.getVideoMetaData(videoId = videoId)
            )
        }
        val data = cachedData.first()
        return YoutubeVideoModel(
            id = data.videoId,
            url = data.videoUrl,
            title = data.title,
            description = "",
            publishedAt = data.publishedAt,
            thumbnailUrl = data.thumbnailUrl,
            channelTitle = data.channelTitle,
            tags = listOf(),
        )
    }

    override suspend fun getVideoScrapedData(url: String) : YoutubeVideoModel {
        return withContext(Dispatchers.IO) {
            val videoId = Utils.parseVideoIdFromUrl(url)
            val cachedVideo = videoDao.getCachedVideoData(videoId)
            if(cachedVideo.isEmpty()) {
                val metadata = metadataExtractor.extractFromUrl(url)
                YoutubeVideoModel(
                    id = videoId,
                    url = url,
                    title = metadata.title,
                    description = metadata.description,
                    thumbnailUrl = metadata.thumbnailUrl,
                )
            }
            val data = cachedVideo.first()
            YoutubeVideoModel(
                id = data.videoId,
                url = data.videoUrl,
                title = data.title,
                description = data.description,
                thumbnailUrl = data.thumbnailUrl,
            )
        }
    }

    override suspend fun getVideoCaptions(
        videoId: String,
        lang: String,
    ): List<YoutubeCaptionModel> {
        val cachedCaptions = videoDao.getCachedVideoCaptions(videoId, lang)
        return if(cachedCaptions.isEmpty()) {
            mapNetworkCaptionsToDomainModel(
                youtubeCaptionsService.getVideoCaptions(videoId = videoId, lang = lang)
            )
        } else {
            mapCachedCaptionsToDomainModel(cachedCaptions)
        }
    }

    override suspend fun addVideoToFavorites(videoId: String, videoUrl: String) {
        withContext(Dispatchers.IO) {
            val metadata = metadataExtractor.extractFromUrl(videoUrl)
            val mappedVideo = YoutubeDataCache(
                videoId = videoId,
                videoUrl = videoUrl,
                title = metadata.title,
                thumbnailUrl = metadata.thumbnailUrl,
            )
            videoDao.addVideoToFavorites(mappedVideo)
        }
    }

    override fun getSavedVideo(videoId: String): Flow<YoutubeVideoModel> {
        return videoDao.getCachedVideoDistinctUntilChanged(videoId).mapLatest {
            if(it.isEmpty()){
                YoutubeVideoModel.Empty()
            } else {
                YoutubeVideoModel(
                    id = it.first().videoId,
                    url = it.first().videoUrl,
                    publishedAt = it.first().publishedAt,
                    title = it.first().title,
                    thumbnailUrl = it.first().thumbnailUrl,
                )
            }
        }
    }

    override suspend fun getVideoCaptionsTracks(videoId: String): List<YoutubeCaptionTracksModel> {
        var tracks = videoDao.getCachedCaptionTracks(videoId).map{
            YoutubeCaptionTracksModel(
                id = it.trackId,
                name = it.name ?: "",
                langCode = it.langCode,
                langOriginal = it.langOriginal ?: "",
                langTranslated = it.langTranslated ?: "",
                langDefault = it.langDefault,
            )
        }
        if(tracks.isEmpty()) {
            tracks = youtubeCaptionsService.getVideoCaptionsTracks(videoId=videoId).tracks?.map{
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
        return tracks
    }

    override fun getSavedVideos(): Flow<List<YoutubeVideoModel>> {
        return videoDao.getCachedVideos().mapLatest { videos ->
            videos.map{
                YoutubeVideoModel(
                    id = it.videoId,
                    url = it.videoUrl,
                    publishedAt = it.publishedAt,
                    title = it.title,
                    thumbnailUrl = it.thumbnailUrl,
                )
            }
        }
    }

    override suspend fun cacheVideoComments(comments: List<YoutubeCommentModel>) {
        val toCache = comments.map{
            YoutubeCommentsCache(
                videoId = it.videoId,
                commentId = it.commentId,
                page = it.page,
                comment = it.content,
                author = it.authorName,
                profileImageUrl = it.authorProfileImageUrl,
                likesCount = it.likeCount,
                dislikesCount = it.dislikesCount,
                publishedAt = it.publishedAt,
            )
        }.toTypedArray()
        videoDao.cacheVideoComments(*toCache)
    }

    override suspend fun cacheVideoCaptions(videoId: String, langCode: String, captions: List<YoutubeCaptionModel>) {
        val toCache = captions.map{
            YoutubeCaptionsCache(
                videoId = videoId,
                lang = langCode,
                start = it.start!!,
                duration = it.duration!!,
                caption = it.caption,
            )
        }.toTypedArray()
        videoDao.cacheVideoCaptions(*toCache)
    }

    override suspend fun cacheVideoCaptionTracks(videoId: String, tracks: List<YoutubeCaptionTracksModel>) {
        val toCache = tracks.map{
            YoutubeCaptionTracksCache(
                videoId = videoId,
                trackId = it.id,
                name = it.name,
                langCode = it.langCode,
                langOriginal = it.langOriginal,
                langTranslated = it.langTranslated,
                langDefault = it.langDefault,
            )
        }.toTypedArray()
        videoDao.cacheVideoCaptionTracks(*toCache)
    }

    override suspend fun removeVideoFromFavorites(videoId: String) {
        videoDao.removeVideoFromFavorites(videoId)
    }

    override fun getCachedVideoComments(videoId: String): Flow<List<YoutubeCommentModel>> {
        return videoDao.getCachedVideoComments(videoId).mapLatest { comments ->
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

    private fun mapCachedCaptionsToDomainModel(captions: List<YoutubeCaptionsCache>) : List<YoutubeCaptionModel> {
        return captions.map{
            YoutubeCaptionModel(
                caption = it.caption,
                start = it.start,
                duration = it.duration,
            )
        }
    }

    private fun mapNetworkCaptionsToDomainModel(captions: CaptionsList) : List<YoutubeCaptionModel> {
        return captions.captions?.map{
            YoutubeCaptionModel(
                caption = it.content,
                start = it.start,
                duration = it.dur,
            )
        } ?: listOf()
    }
}