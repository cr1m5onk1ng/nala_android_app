package com.example.nala.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.nala.db.models.yt.YoutubeCaptionsCache
import com.example.nala.db.models.yt.YoutubeCommentsCache
import com.example.nala.db.models.yt.YoutubeDataCache
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Query("SELECT * FROM videos_cache WHERE video_id=:videoId")
    suspend fun getCachedVideo(videoId: String) : YoutubeDataCache

    @Query("SELECT * FROM videos_cache ORDER BY time_added DESC")
    fun getCachedVideos() : Flow<List<YoutubeDataCache>>

    @Query("SELECT * FROM video_comments_cache WHERE video_id=:videoId ORDER BY published_at DESC")
    fun getVideoComments(videoId: String) : Flow<List<YoutubeCommentsCache>>

    @Query("SELECT * FROM video_captions_cache WHERE video_id=:videoId AND lang=:lang ORDER BY start")
    suspend fun getVideoCaptions(videoId: String, lang: String) : List<YoutubeCaptionsCache>

}