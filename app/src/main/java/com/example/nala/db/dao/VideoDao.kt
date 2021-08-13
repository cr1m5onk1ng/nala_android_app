package com.example.nala.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nala.db.models.yt.YoutubeCaptionsCache
import com.example.nala.db.models.yt.YoutubeCommentsCache
import com.example.nala.db.models.yt.YoutubeDataCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface VideoDao {

    @Query("SELECT * FROM videos_cache WHERE video_id=:videoId")
    fun getCachedVideo(videoId: String) : Flow<List<YoutubeDataCache>>

    fun getCachedVideoDistinctUntilChanged(videoId: String) =
        getCachedVideo(videoId).distinctUntilChanged()

    @Query("SELECT * FROM videos_cache ORDER BY time_added DESC")
    fun getCachedVideos() : Flow<List<YoutubeDataCache>>

    @Query("SELECT * FROM video_comments_cache WHERE video_id=:videoId ORDER BY published_at DESC")
    fun getVideoComments(videoId: String) : Flow<List<YoutubeCommentsCache>>

    @Query("SELECT * FROM video_captions_cache WHERE video_id=:videoId AND lang=:lang ORDER BY start")
    suspend fun getVideoCaptions(videoId: String, lang: String) : List<YoutubeCaptionsCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addVideoToFavorites(video: YoutubeDataCache)

    @Query("DELETE FROM videos_cache WHERE video_id=:videoId")
    suspend fun removeVideoFromFavorites(videoId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheVideoCaptions(vararg captions: YoutubeCaptionsCache)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheVideoComments(vararg comments: YoutubeCommentsCache)

}