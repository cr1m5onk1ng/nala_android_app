package com.example.nala.repository

import com.example.nala.BuildConfig
import com.example.nala.db.dao.VideoDao
import com.example.nala.db.models.yt.YoutubeCaptionTracksCache
import com.example.nala.db.models.yt.YoutubeCaptionsCache
import com.example.nala.db.models.yt.YoutubeCommentsCache
import com.example.nala.db.models.yt.YoutubeDataCache
import com.example.nala.domain.model.metadata.MetadataModel
import com.example.nala.domain.model.yt.*
import com.example.nala.network.model.yt.captions.CaptionsList
import com.example.nala.network.services.YouTubeApiService
import com.example.nala.network.services.YoutubeCaptionsService
import com.example.nala.services.metadata.AsyncExtractorService
import com.example.nala.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class YoutubeRepositoryImpl @Inject constructor(
    private val youtubeCaptionsService: YoutubeCaptionsService,
    private val youTubeApiService: YouTubeApiService,
    private val videoDao: VideoDao,
    private val metadataExtractor: AsyncExtractorService<MetadataModel>,
    ) : YouTubeRepository {

    private val commentMapper = YoutubeCommentMapper()
    private val videoMapper = YoutubeVideoMapper()

    override suspend fun getVideoComments(videoId: String, key: String?, pageId: String?): YoutubeCommentsList {
        val cachedComments = videoDao.getCachedVideoComments(videoId, pageId)
        if(cachedComments.isNotEmpty()) {
            val comments = cachedComments.map{ comment ->
                YoutubeCommentModel(
                    videoId = comment.videoId,
                    commentId = comment.commentId,
                    content = comment.comment,
                    publishedAt = comment.publishedAt ?: "",
                    authorName = comment.author,
                    authorProfileImageUrl = comment.profileImageUrl,
                    likeCount = comment.likesCount ?: 0,
                    dislikesCount = 0,
                    replies = videoDao.getCommentResponses(comment.commentId).map{
                        YoutubeCommentModel(
                            videoId = it.videoId,
                            commentId = it.commentId,
                            content = it.comment,
                            publishedAt = it.publishedAt ?: "",
                            authorName = it.author,
                            authorProfileImageUrl = it.profileImageUrl,
                            likeCount = it.likesCount ?: 0,
                            dislikesCount = 0,
                        )
                    }
                )
            }
            return YoutubeCommentsList(
                comments = comments,
            )
        } else {
            val comments = if(key == null) {
                commentMapper.mapToDomainModel(
                    youTubeApiService.getVideoTopComments(
                        videoId = videoId,
                        key=BuildConfig.YT_DATA_API_KEY,
                        pageToken = pageId,
                        maxResults=20)
                )
            } else {
                commentMapper.mapToDomainModel(
                    youTubeApiService.getVideoTopCommentsWithToken(
                        videoId = videoId,
                        accessToken="Bearer $key",
                        pageToken = pageId,
                        maxResults=20)
                )
            }
            cacheVideoComments(comments.comments)
            return comments
        }
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
            val captions = mapNetworkCaptionsToDomainModel(
                youtubeCaptionsService.getVideoCaptions(videoId = videoId, lang = lang)
            )
            cacheVideoCaptions(videoId, lang, captions)
            captions
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

    @ExperimentalCoroutinesApi
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
            if(tracks.isNotEmpty()) cacheVideoCaptionTracks(videoId, tracks)
        }
        return tracks
    }

    @ExperimentalCoroutinesApi
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

    @ExperimentalCoroutinesApi
    override fun getCachedVideoComments(videoId: String): Flow<List<YoutubeCommentModel>> {
        return videoDao.getCachedVideoCommentsFlow(videoId).mapLatest { comments ->
            comments.map{
                YoutubeCommentModel(
                    videoId = it.videoId,
                    commentId = it.commentId,
                    content = it.comment,
                    publishedAt = it.publishedAt ?: "",
                    authorName = it.author,
                    authorProfileImageUrl = it.profileImageUrl,
                    dislikesCount = it.dislikesCount,
                    likeCount = it.likesCount,
                )
            }
        }
    }

    override suspend fun removeVideoFromFavorites(videoId: String) {
        videoDao.removeVideoFromFavorites(videoId)
    }

    private suspend fun cacheVideoComments(comments: List<YoutubeCommentModel>) {
        val toCache = comments.map{
            YoutubeCommentsCache(
                videoId = it.videoId,
                commentId = it.commentId,
                pageId = it.pageId,
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

    private suspend fun cacheVideoCaptions(videoId: String, langCode: String, captions: List<YoutubeCaptionModel>) {
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

    private suspend fun cacheVideoCaptionTracks(videoId: String, tracks: List<YoutubeCaptionTracksModel>) {
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