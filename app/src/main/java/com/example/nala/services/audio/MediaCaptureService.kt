package com.example.nala.services.audio

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import androidx.lifecycle.LifecycleService
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjection
import androidx.core.app.NotificationCompat
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.concurrent.thread
import kotlin.experimental.and

class MediaCaptureService : LifecycleService() {

    companion object {
        private const val LOG_TAG = "AudioCaptureService"
        private const val SERVICE_ID = 123
        private const val NOTIFICATION_CHANNEL_ID = "AudioCapture channel"

        private const val NUM_SAMPLES_PER_READ = 1024
        private const val BYTES_PER_SAMPLE = 2 // 2 bytes since we hardcoded the PCM 16-bit format
        private const val BUFFER_SIZE_IN_BYTES = NUM_SAMPLES_PER_READ * BYTES_PER_SAMPLE

        const val ACTION_START = "AudioCaptureService:Start"
        const val ACTION_STOP = "AudioCaptureService:Stop"
        const val EXTRA_RESULT_DATA = "AudioCaptureService:Extra:ResultData"

        // SPEECH2TEXT CONSTANTS
        private const val AUDIO_LEN_IN_SECOND = 6
        private const val SAMPLE_RATE = 16000
        private const val RECORDING_LENGTH = SAMPLE_RATE * AUDIO_LEN_IN_SECOND
    }

    // MEDIA MANAGER AND RECORD
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private lateinit var audioCaptureThread: Thread
    private var audioRecord: AudioRecord? = null

    // MODEL VARIABLES
    // lateinit var s2tmodel: Speech2TextModel

    // STATE VARIABLES
    //private val currentTranscriptState = MutableStateFlow("")
    //val currentTranscript = currentTranscriptState.asStateFlow()


    override fun onCreate() {
        super.onCreate()

        // TODO: Add code for starting the service and getting notifications
        createNotificationChannel()

        startForeground(SERVICE_ID, NotificationCompat.Builder(this,
            NOTIFICATION_CHANNEL_ID).build())

        mediaProjectionManager = applicationContext.getSystemService(
            Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    private fun createNotificationChannel() {
        val serviceChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Audio Capture Service Channel", NotificationManager.IMPORTANCE_DEFAULT
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val manager = getSystemService(NotificationManager::class.java) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return if (intent != null) {
            when (intent.action) {
                ACTION_START -> {
                    mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, intent.getParcelableExtra(EXTRA_RESULT_DATA)!!) as MediaProjection

                    startAudioCapture()
                    Service.START_STICKY
                }
                ACTION_STOP -> {
                    stopAudioCapture()
                    Service.START_NOT_STICKY
                }
                else -> throw IllegalArgumentException("Unexpected action received: ${intent.action}")
            }
        } else {
            Service.START_NOT_STICKY
        }
    }

    private fun startAudioCapture() {
        // TODO: add code for executing audio capture itself
        val config = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AudioPlaybackCaptureConfiguration.Builder(mediaProjection!!)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        } else {
            TODO("VERSION.SDK_INT < Q")
        }

        val audioFormat = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(SAMPLE_RATE)
            .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
            .build()

        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord.Builder()
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(bufferSize)
            .setAudioPlaybackCaptureConfig(config)
            .build()

        audioRecord!!.startRecording()

        audioCaptureThread = thread(start = true) {
            val outputFile = createAudioFile()
            Log.d(LOG_TAG, "Created file for capture target: ${outputFile.absolutePath}")
            writeAudioToFile(outputFile)
        }
    }

    /*
    private fun generateTranscriptFromRecord(record: AudioRecord?, bufferSize: Int) {
        var shortsRead: Long = 0
        var recordingOffset = 0
        val audioBuffer = ShortArray(bufferSize / 2)
        val recordingBuffer = ShortArray(RECORDING_LENGTH)

        while (shortsRead < RECORDING_LENGTH) {
            val numberOfShort = record!!.read(audioBuffer, 0, audioBuffer.size)
            shortsRead += numberOfShort.toLong()
            System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, numberOfShort)
            recordingOffset += numberOfShort
        }
        val floatInputBuffer = FloatArray(RECORDING_LENGTH)

        // feed in float values between -1.0f and 1.0f by dividing the signed 16-bit inputs.

        // feed in float values between -1.0f and 1.0f by dividing the signed 16-bit inputs.
        for (i in 0 until RECORDING_LENGTH) {
            floatInputBuffer[i] = recordingBuffer[i] / Short.MAX_VALUE.toFloat()
        }
        val result: String = s2tmodel.predict(floatInputBuffer)
        currentTranscriptState.value = result
    } */

    private fun createAudioFile(): File {
        val audioCapturesDirectory = File(getExternalFilesDir(null), "/AudioCaptures")
        if (!audioCapturesDirectory.exists()) {
            audioCapturesDirectory.mkdirs()
        }

        val timestamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("dd-MM-yyyy-hh-mm-ss", Locale.US).format(Date())
        } else {
            TODO("VERSION.SDK_INT < N")
        }

        val fileName = "Capture-$timestamp.pcm"
        return File(audioCapturesDirectory.absolutePath + "/" + fileName)
    }

    private fun writeAudioToFile(outputFile: File) {
        val fileOutputStream = FileOutputStream(outputFile)
        val capturedAudioSamples = ShortArray(NUM_SAMPLES_PER_READ)

        while (!audioCaptureThread.isInterrupted) {
            audioRecord?.read(capturedAudioSamples, 0, NUM_SAMPLES_PER_READ)

            fileOutputStream.write(
                capturedAudioSamples.toByteArray(),
                0,
                BUFFER_SIZE_IN_BYTES
            )
        }

        fileOutputStream.close()
        Log.d(LOG_TAG, "Audio capture finished for ${outputFile.absolutePath}. File size is ${outputFile.length()} bytes.")
    }

    private fun stopAudioCapture() {
        requireNotNull(mediaProjection) { "Tried to stop audio capture, but there was no ongoing capture in place!" }

        audioCaptureThread.interrupt()
        audioCaptureThread.join()

        audioRecord!!.stop()
        audioRecord!!.release()
        audioRecord = null

        mediaProjection!!.stop()
        stopSelf()
    }

    private fun ShortArray.toByteArray(): ByteArray {
        val bytes = ByteArray(size * 2)
        for (i in 0 until size) {
            bytes[i * 2] = (this[i] and 0x00FF).toByte()
            bytes[i * 2 + 1] = (this[i].toInt() shr 8).toByte()
            this[i] = 0
        }
        return bytes
    }

}