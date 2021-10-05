package com.example.nala.ui.dictionary

import android.app.*
import android.content.Context
import android.os.Build
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.example.nala.R
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.nala.db.models.kanji.KanjiStories
import com.example.nala.repository.DictionaryRepository
import com.example.nala.repository.KanjiRepository
import com.example.nala.repository.ReviewRepository
import com.example.nala.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DictionaryForegroundService : LifecycleService() {

    private lateinit var dictionaryWindow: DictionaryWindow
    @Inject lateinit var dictRepository: DictionaryRepository
    @Inject lateinit var kanjiRepository: KanjiRepository
    @Inject lateinit var reviewRepository: ReviewRepository

    override fun onCreate() {
        super.onCreate()

        dictionaryWindow = DictionaryWindow(
            applicationContext,
            reviewRepository,
            lifecycleScope
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let{
            if(it.action == Constants.ACTION_STOP_DICTIONARY) {
                stopForeground(true)
                stopSelf()
                return START_NOT_STICKY
            }
        }
        val word = intent?.getStringExtra("word") ?: ""
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startCustomForeground()
        else
            startForeground(1, Notification())
        lifecycleScope.launch{
            dictionaryWindow.setLoadingScreen()
            val wordData = dictRepository.search(word)
            if(wordData.isEmpty()) {
                dictionaryWindow.setErrorScreen()
            } else {
                val kanjis = kanjiRepository.getWordKanjis(wordData.word)
                val stories = mutableListOf<KanjiStories>()
                val wordReviews = reviewRepository.getWordReviewsAsString() as MutableList<String>
                val kanjiReviews = reviewRepository.getKanjiReviewsAsString() as MutableList<String>
                kanjis.forEach {
                    val story = kanjiRepository.getKanjiStory(it.kanji)
                    stories.add(KanjiStories(kanji=it.kanji, story=story))
                }
                dictionaryWindow.setWordData(wordData, kanjis, stories, wordReviews, kanjiReviews)
                dictionaryWindow.openWordDict()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        dictionaryWindow.closeDictionaryView()
    }

    private fun startCustomForeground() {
        Log.d("DICTIONARYWINDOW", "Custom Foreground STARTED!")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val stopDictServiceIntent = Intent(applicationContext, DictionaryForegroundService::class.java).apply{
                action = Constants.ACTION_STOP_DICTIONARY
            }
            val stopPendingIntent = PendingIntent.getService(
                this,
                1,
                stopDictServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT,
            )
            val NOTIFICATION_CHANNEL_ID = "com.example.nala"
            val channelName = "dictionaryService"
            val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setDeleteIntent(stopPendingIntent)
                .addAction(R.drawable.ic_baseline_close_24, "close dictionary", stopPendingIntent)
                .build();
            startForeground(2, notification)
        }
    }

}



