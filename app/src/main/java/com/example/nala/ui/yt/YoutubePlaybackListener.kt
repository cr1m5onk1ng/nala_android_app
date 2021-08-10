package com.example.nala.ui.yt

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener

class YoutubePlaybackListener(
    private val onPlayerTimeElapsed: (Float) -> Unit,
) : YouTubePlayerListener {
    override fun onApiChange(youTubePlayer: YouTubePlayer) {
        return
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        onPlayerTimeElapsed(second)
    }

    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
        return
    }

    override fun onPlaybackQualityChange(
        youTubePlayer: YouTubePlayer,
        playbackQuality: PlayerConstants.PlaybackQuality
    ) {
        return
    }

    override fun onPlaybackRateChange(
        youTubePlayer: YouTubePlayer,
        playbackRate: PlayerConstants.PlaybackRate
    ) {
        return
    }

    override fun onReady(youTubePlayer: YouTubePlayer) {
        return
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        return
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        return
    }

    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
        return
    }

    override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
        return
    }
}