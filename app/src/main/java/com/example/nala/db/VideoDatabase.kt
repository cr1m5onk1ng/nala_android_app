package com.example.nala.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nala.db.converters.TimeConverter
import com.example.nala.db.dao.VideoDao
import com.example.nala.db.models.yt.YoutubeCaptionsCache
import com.example.nala.db.models.yt.YoutubeCommentsCache
import com.example.nala.db.models.yt.YoutubeDataCache

@Database(
    entities = [
        YoutubeDataCache::class,
        YoutubeCaptionsCache::class,
        YoutubeCommentsCache::class,
    ],
    version = 2,
)
@TypeConverters(TimeConverter::class)
abstract class VideoDatabase : RoomDatabase() {

    abstract fun videoDao() : VideoDao

    companion object {
        val DATABASE_NAME = "video_database"
    }
}