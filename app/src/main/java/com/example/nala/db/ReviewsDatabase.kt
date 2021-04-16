package com.example.nala.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.WordDefinitionDto
import com.example.nala.db.models.review.WordReviewModelDto
import com.example.nala.db.models.review.WordTagDto

@Database(entities = [
    WordReviewModelDto::class,
    WordDefinitionDto::class,
    WordTagDto::class],
    version=2)
abstract class ReviewDatabase : RoomDatabase() {
    abstract  fun reviewDao() : ReviewDao

    companion object {
        val DATABASE_NAME: String = "review_database"
    }
}