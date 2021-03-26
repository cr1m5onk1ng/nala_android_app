package com.example.nala.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.nala.R
import com.example.nala.db.dao.ReviewDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.nala.ui.dictionary.DictionaryViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var app: BaseApplication

    @Inject
    lateinit var dao: ReviewDao

    private val viewModel: DictionaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.action != null && intent.action.equals(Intent.ACTION_PROCESS_TEXT) ){
            if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)){
                viewModel.setSharedText(intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) )
            }
        }
        setContentView(R.layout.activity_main)
    }
}