package com.example.nala.service.background

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.example.nala.db.models.review.Articles
import com.example.nala.repository.ReviewRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import javax.inject.Inject

class ArticleService @Inject constructor(
    private val reviewRepository: ReviewRepository
) : Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra("url").let{ link ->
            if(link != null) {
                addArticleToFavorites(link)
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addArticleToFavorites(url: String) {
        val article = Articles(
            url = url,
            timeAdded = Date.from(Instant.now())
        )
        scope.launch{
            reviewRepository.addArticleToFavorites(url)
        }
    }
}