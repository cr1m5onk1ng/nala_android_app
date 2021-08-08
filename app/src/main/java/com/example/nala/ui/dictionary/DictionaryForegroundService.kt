package com.example.nala.ui.dictionary

import android.app.NotificationManager
import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.example.nala.R
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.nala.db.models.kanji.KanjiStories
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.repository.DictionaryRepository
import com.example.nala.repository.KanjiRepository
import com.example.nala.repository.ReviewRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class DictionaryForegroundService : LifecycleService() {

    private lateinit var dictionaryWindow: DictionaryWindow
    @Inject lateinit var dictRepository: DictionaryRepository
    @Inject lateinit var kanjiRepository: KanjiRepository
    @Inject lateinit var reviewRepository: ReviewRepository
    private lateinit var viewModel: DictionaryViewModel

    override fun onCreate() {
        super.onCreate()

        viewModel = DictionaryViewModel(
            dictRepository,
            kanjiRepository,
            reviewRepository,
            applicationContext
        )

        dictionaryWindow = DictionaryWindow(
            applicationContext,
            reviewRepository,
            lifecycleScope
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val word = intent?.getStringExtra("word") ?: ""
        Log.d("DICTIONARYWINDOW", "Inside Service!")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startCustomForeground()
        else
            startForeground(1, Notification())
        lifecycleScope.launch{
            dictionaryWindow.setLoadingScreen()
            val wordData = dictRepository.search(word)
            val kanjis = kanjiRepository.getWordKanjis(wordData.word)
            val stories = mutableListOf<KanjiStories>()
            var wordReviews = reviewRepository.getWordReviewsAsString() as MutableList<String>
            var kanjiReviews = reviewRepository.getKanjiReviewsAsString() as MutableList<String>
            /*
            reviewRepository.getWordReviews().collect{ reviews ->
                wordReviews = reviews.map{ it.word }
            }
            reviewRepository.getAllKanjiReviewItems().collect{ reviews ->
                kanjiReviews = reviews.map{ it.kanji }
            } */
            kanjis.forEach {
                val story = kanjiRepository.getKanjiStory(it.kanji)
                stories.add(KanjiStories(kanji=it.kanji, story=story))
            }
            dictionaryWindow.setWordData(wordData, kanjis, stories, wordReviews, kanjiReviews)
            dictionaryWindow.openWordDict()
            //dictionaryWindow.openKanjiDict()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        dictionaryWindow.close()
    }

    private fun startCustomForeground() {
        Log.d("DICTIONARYWINDOW", "Custom Foreground STARTED!")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = "com.example.nala"
            val channelName = "dictionaryService"
            val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)!!
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
            startForeground(2, notification)
        }
    }

}



