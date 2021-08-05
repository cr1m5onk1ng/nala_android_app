package com.example.nala.ui.dictionary

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.nala.R
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.repository.DictionaryRepository
import com.example.nala.ui.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DictionaryLifecycleService : LifecycleService() {

    @Inject lateinit var dictRepository: DictionaryRepository
    lateinit var dictionaryWindow: DictionaryWindow

    private val _wordState =
        MutableStateFlow<DataState<DictionaryModel>>(DataState.Initial(DictionaryModel.Empty()))

    val wordState: StateFlow<DataState<DictionaryModel>> = _wordState

    override fun onCreate() {
        super.onCreate()
        dictionaryWindow = DictionaryWindow(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("DICTIONARYWINDOW", "Inside Service!")
        val word = intent?.getStringExtra("word")
        searchWord(word ?: "")
        //val bundle = intent?.extras
        //val word: DictionaryModel = bundle?.getParcelable("word") ?: DictionaryModel.Empty()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startCustomForeground()
        else
            startForeground(1, Notification())
        //dictionaryWindow.open(word)
        setScreen()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        dictionaryWindow.close()
    }

    private fun setScreen() {
        Log.d("DICTIONARYWINDOW", "Inside setScreen!")
        lifecycleScope.launch{
            wordState.collect {
                when(it){
                    is DataState.Initial<*>, DataState.Loading -> {
                        Log.d("DICTIONARYWINDOW", "Data LOADING")
                        dictionaryWindow.setLoadingScreen()
                    }
                    is DataState.Error -> {
                        Log.d("DICTIONARYWINDOW", "Data ERROR")
                    }
                    is DataState.Success<DictionaryModel> -> {
                        val data = it.data
                        Log.d("DICTIONARYWINDOW", "Data SUCCESS. Data: $data")
                        dictionaryWindow.open(data)
                    }
                }
            }
        }
    }

    private fun searchWord(word: String) {
        lifecycleScope.launch{
            _wordState.value = DataState.Loading
            dictRepository.searchFlow(word).collect {
                _wordState.value = DataState.Success(it)
            }
        }
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