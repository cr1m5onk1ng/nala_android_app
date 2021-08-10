package com.example.nala.ui.adapters.yt

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nala.ui.yt.YoutubeCaptionsFragment
import com.example.nala.ui.yt.YoutubeCommentsFragment

class YoutubeFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> {
               return YoutubeCommentsFragment()
            }
            1 -> {
               return YoutubeCaptionsFragment()
            }
        }
        return YoutubeCommentsFragment()
    }
}