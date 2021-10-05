package com.example.nala.ui.ocr

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.nala.R
import com.example.nala.services.ocr.FallbackScreenshotService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OCRForegroundService : Service() {
    inner class OCRServiceBinder() : Binder() {
        fun getService() : OCRForegroundService = this@OCRForegroundService
    }

    companion object {
        const val ACTION_STOP_OCR = "ACTION_STOP_OCR"
    }

    var screenshotService: FallbackScreenshotService? = null

    private val ocrServiceBinder = OCRServiceBinder()

    private lateinit var ocrWindow: OCRWindow

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate() {
        super.onCreate()
        Log.d("OCR_DEBUG", "OCR SERVICE CREATED")
        ocrWindow = OCRWindow(applicationContext, ::recognize)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        screenshotService?.dispose()
    }

    override fun onBind(p0: Intent?): IBinder {
        return ocrServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("OCR_DEBUG", "OCR SERVICE STARTED!")
        intent?.let{
            if(it.action == ACTION_STOP_OCR) {
                stopForeground(true)
                stopSelf()
                return START_NOT_STICKY
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startCustomIntent()
        else
            startForeground(1, Notification())
        ocrWindow.initWindow()
        return START_STICKY
    }

    /*
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    } */

    fun setOcrScreenshotService(service: FallbackScreenshotService) {
        screenshotService = service
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        screenshotService?.onActivityResult(requestCode, resultCode, data)
    }

    private fun recognize() {
        assert(screenshotService != null)
        scope.launch{
            ocrWindow.setLoadingScreen()
            screenshotService!!.recognize()
            val recognizedText = screenshotService!!.getResult()
            Log.d("OCR_DEBUG", "Recognized Text Inside Service: $recognizedText")
            ocrWindow.setRecognizedText(recognizedText)
        }
    }

    private fun startCustomIntent() {
        Log.d("OCR_DEBUG", "Custom Foreground STARTED!")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val stopOcrServiceIntent = Intent(applicationContext, OCRForegroundService::class.java).apply{
                action = ACTION_STOP_OCR
            }
            val stopPendingIntent = PendingIntent.getService(
                this,
                1,
                stopOcrServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT,
            )
            val NOTIFICATION_CHANNEL_ID = "com.example.nala.ocr"
            val channelName = "ocrService"
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
                .addAction(R.drawable.ic_baseline_close_24, "close ocr", stopPendingIntent)
                .build();
            startForeground(2, notification)
        }
    }
}