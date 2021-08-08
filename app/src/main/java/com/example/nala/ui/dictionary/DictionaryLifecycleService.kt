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
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.ui.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DictionaryLifecycleService : LifecycleService() {

    private lateinit var dictionaryWindow: DictionaryPopUp
    @Inject lateinit var viewModel: DictServiceViewModel

    override fun onCreate() {
        super.onCreate()

        dictionaryWindow = DictionaryPopUp(
            applicationContext,
            viewModel,
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
        viewModel.searchWord(word)
        when(viewModel.currentWord.value){
            is DataState.Initial<*>, DataState.Loading -> {
                dictionaryWindow.setLoadingScreen()
            }
            is DataState.Success -> {
                dictionaryWindow.setWordData(
                    word = (viewModel.currentWord.value as DataState.Success<DictionaryModel>).data,
                    kanjis = viewModel.currentWordKanjis.value,
                    stories = viewModel.kanjiStories.value,
                )
                dictionaryWindow.openWordDict()
            }
            else -> {

            }
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
                .setContentTitle("NaLa is running in background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
            startForeground(2, notification)
        }
    }

}



