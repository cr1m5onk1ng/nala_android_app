package com.example.nala.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.nala.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.nala.ui.dictionary.DictionaryViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var app: BaseApplication

    private val viewModel: DictionaryViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)){
                    viewModel.setSharedText(intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) )
                }
            }
            Intent.ACTION_SEND -> {
                Log.d("SHARED", "ACTION SEND CALLED!")
                if ("text/plain" == intent.type) {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                        Log.d("SHARED", "SHARED TEXT: $it")
                        viewModel.setSharedSentence(it)
                    }
                } else {
                    Log.d("SHARED", "DIDNT PROCESS TEXT!")
                }
            }
            else -> {
                // DONT BOTHER
            }
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main)
    }
}