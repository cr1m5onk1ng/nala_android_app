package com.example.nala.services.audio

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.ArrayList
import javax.inject.Inject

class AudioFilesManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    // TODO: add a function to retrieve the recordings from memory instead of the hardcoded dummy one
    fun getSavedRecordings(
        audioDir: String = "/AudioCaptures"
    ) : List<Pair<File, Int>> {

        val items: MutableList<Pair<File, Int>> = ArrayList()
        val files = File(context.getExternalFilesDir(null), audioDir)
        if (files.listFiles() != null) {
            val file : Array<File> = files.listFiles()!!
            for (i in file.indices) {
                items.add(Pair(file[i], i))
            }
        } else {
            Log.d("AUDIO_DEBUG", "There are no files")
        }
        return items
    }
}