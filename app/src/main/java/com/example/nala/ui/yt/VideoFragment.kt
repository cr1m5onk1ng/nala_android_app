package com.example.nala.ui.yt

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoFragment : Fragment() {

    private val ytViewModel: YoutubeViewModel by activityViewModels()

}