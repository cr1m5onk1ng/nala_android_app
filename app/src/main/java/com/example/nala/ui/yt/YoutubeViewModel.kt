package com.example.nala.ui.yt

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.domain.model.yt.YoutubeCaptionModel
import com.example.nala.domain.model.yt.YoutubeCommentModel
import com.example.nala.domain.model.yt.YoutubeCommentsList
import com.example.nala.network.model.yt.captions.CaptionsMapEntry
import com.example.nala.repository.DictionaryRepository
import com.example.nala.repository.ReviewRepository
import com.example.nala.repository.YouTubeRepository
import com.example.nala.service.tokenization.TokenizerService
import com.example.nala.ui.DataState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YoutubeViewModel @Inject constructor(
    private val youtubeRepository: YouTubeRepository,
    private val dictRepository: DictionaryRepository,
) : ViewModel() {

    val ytPlayer: MutableState<YouTubePlayer?> = mutableStateOf(null)

    private val _currentVideoId = MutableStateFlow<String>("")

    val currentVideoId: StateFlow<String> = _currentVideoId

    private val videoCaptionsStateFlow = _currentVideoId.mapLatest {
        youtubeRepository.getVideoCaptions(it)
    }

    private val videoCommentsStateFlow = _currentVideoId.mapLatest {
        youtubeRepository.getVideoComments(it)
    }

    val captionsState: MutableState<DataState<List<YoutubeCaptionModel>>> =
        mutableStateOf(DataState.Initial(listOf()))

    val commentsState: MutableState<DataState<YoutubeCommentsList>> =
        mutableStateOf(DataState.Initial(YoutubeCommentsList.Empty()))

    val activeCaption: MutableState<Int> = mutableStateOf(0)

    val captionsMap: MutableState<Map<Int, List<CaptionsMapEntry>>> = mutableStateOf(mapOf())

    var currentPlayerPosition: MutableState<Float> = mutableStateOf(0f)

    val listState: MutableState<LazyListState> =
        mutableStateOf(LazyListState( firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0))

    val selectedTab: MutableState<Int> = mutableStateOf(0)

    private val _inspectedComment = MutableStateFlow<YoutubeCommentModel?>(null)

    val inspectedComment: StateFlow<YoutubeCommentModel?> = _inspectedComment

    private val _inspectedCaption = MutableStateFlow<YoutubeCaptionModel?>(null)

    val inspectedCaption: StateFlow<YoutubeCaptionModel?> = _inspectedCaption

    val inspectedCaptionIndex: MutableState<Int?> = mutableStateOf(null)

    val inspectedCommentIndex: MutableState<Int?> = mutableStateOf(null)

    val inspectedElementTokens: MutableState<List<String>> = mutableStateOf(listOf())

    val inspectedElementTokensMap : MutableState<Map<Pair<Int, Int>, String>> = mutableStateOf(mapOf())

    val inspectedElementSelectedWord: MutableState<String> = mutableStateOf("")

    private val inspectedCaptionTokensFlow = _inspectedCaption.mapLatest { caption ->
        caption?.caption?.let{
           dictRepository.tokenize(it)
        }
    }

    private val inspectedCommentTokensFlow = _inspectedComment.mapLatest { comment ->
        comment?.content?.let{
            dictRepository.tokenize(it)
        }
    }

    fun setSelectedWord(word: String) {
        inspectedElementSelectedWord.value = word
    }

    fun onInspectComment(comment: YoutubeCommentModel) {
        viewModelScope.launch {
            _inspectedComment.value = comment
            inspectedCommentTokensFlow.collect {
                inspectedElementTokens.value = it ?: listOf()
                inspectedElementTokensMap.value = dictRepository.tokensToIndexMap(
                    tokens = it ?: listOf(),
                    text = inspectedComment.value?.content ?: ""
                )
            }
        }
    }

    fun onInspectCaption(caption: YoutubeCaptionModel) {
        viewModelScope.launch {
            _inspectedCaption.value = caption
            inspectedCaptionTokensFlow.collect {
                inspectedElementTokens.value = it ?: listOf()
                inspectedElementTokensMap.value = dictRepository.tokensToIndexMap(
                    tokens = it ?: listOf(),
                    text = inspectedCaption.value?.caption ?: ""
                )
            }
        }
    }

    fun initPlayer(player: YouTubePlayer) {
        ytPlayer.value = player
    }

    fun setSelectedTab(index: Int) {
        selectedTab.value = index
    }

    fun setVideoId(videoId: String) {
        _currentVideoId.value = videoId
    }

    fun setPlayerPosition(position: Float) {
        currentPlayerPosition.value = position
    }

    fun loadCaptions() = viewModelScope.launch{
        Log.d("YOUTUBEDEBUG", "INSIDE LOAD CAPTIONS")
        captionsState.value = DataState.Loading
        try{
            videoCaptionsStateFlow.collect {
                captionsState.value = DataState.Success(it)
                captionsMap.value = buildCaptionsMap(it)
                Log.d("YOUTUBEDEBUG", "Current captions: $it")
            }
        } catch(e: Exception){
            Log.d("YOUTUBEDEBUG", "SOMETHING WENT WRONG: $e")
            captionsState.value = DataState.Error("Couldn't fetch captions: $e")
        }
    }

    fun loadComments() = viewModelScope.launch{
        commentsState.value = DataState.Loading
        try{
            videoCommentsStateFlow.collect{
                commentsState.value = DataState.Success(it)
                Log.d("YOUTUBEDEBUG", "Current comments: $it")
            }
        } catch(e: Exception){
            commentsState.value = DataState.Error("Couldn't fetch comments: $e")
        }
    }

    fun onPlayerTimeElapsed(secondsElapsed: Float){
        val currentCaption = bucketSearch(secondsElapsed)
        currentCaption?.let{
            activeCaption.value = it.index
        }
    }

    fun onSeekTo(youTubePlayer: YouTubePlayer, caption: YoutubeCaptionModel) {
        caption.start?.let{
            youTubePlayer.seekTo(it)
        }
    }

    private fun buildCaptionsMap(captions: List<YoutubeCaptionModel>)
        : MutableMap<Int, MutableList<CaptionsMapEntry>>
    {
        val captionsMap: MutableMap<Int, MutableList<CaptionsMapEntry>> = mutableMapOf()
        captions.forEachIndexed{ i, cap ->
            val key = cap.start?.toInt() ?: 0
            val captionEntry = CaptionsMapEntry(
                captionData = cap,
                index = i,
            )
            if(captionsMap[key] == null) {
                captionsMap[key] = mutableListOf(captionEntry)
            } else {
                captionsMap[key]!!.add(captionEntry)
            }
        }
        return captionsMap
    }

    private fun bucketSearch(
        currentPlayerPosition: Float,
    ) : CaptionsMapEntry?
    {
        val index = currentPlayerPosition.toInt()
        Log.d("YOUTUBEDEBUG", "Current player position: $currentPlayerPosition")
        Log.d("YOUTUBEDEBUG", "Current map index: $index")
        val bucket = captionsMap.value[index]
        Log.d("YOUTUBEDEBUG", "Current bucket: $bucket")
        bucket?.forEach {
            if(
                currentPlayerPosition >= it.captionData.start!! &&
                 currentPlayerPosition < it.captionData.start + it.captionData.duration!! ) {
                return it
            }
        }
        return null
    }

}