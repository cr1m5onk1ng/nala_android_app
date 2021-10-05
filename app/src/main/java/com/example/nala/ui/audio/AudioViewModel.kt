package com.example.nala.ui.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.example.nala.services.audio.AudioFilesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileNotFoundException
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val filesManager: AudioFilesManager,
) : ViewModel() {

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        const val SAMPLING_RATE = 8000
    }

    private var isPlaying = false

    // TODO: add the items variable
    private var items: List<Pair<File, Int>> = ArrayList()

    private fun playShortAudioFileViaAudioTrack(file: File) {
        try {
            val inputStream = file.inputStream()

            val bytes = ByteArray(inputStream.available())

            inputStream.read(bytes)

            val audioTrack = configureAudioTrack()

            isPlaying = true

            audioTrack.play()

            audioTrack.write(bytes, 0, bytes.size)

            audioTrack.stop()

            resetUI()

            isPlaying = false

            audioTrack.flush()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun playSound(file: File) {
        Thread {
            playShortAudioFileViaAudioTrack(file)
        }.start()
    }

    private fun resetUI() {
        Handler(Looper.getMainLooper()).post(Runnable {
            items = filesManager.getSavedRecordings()
            //viewAdapter.notifyDataSetChanged()
        })
    }

    private fun configureAudioTrack() : AudioTrack {
        val size = AudioTrack.getMinBufferSize(
            SAMPLING_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT)

        val audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
                .setAudioFormat(
                    AudioFormat.Builder()
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLING_RATE)
                    .build())
                .setBufferSizeInBytes(size)
                .build()
        } else {
            TODO("VERSION.SDK_INT < M")
        }

        audioTrack.playbackRate = SAMPLING_RATE

        val params = audioTrack.playbackParams

        audioTrack.playbackParams = params

        return audioTrack
    }

}