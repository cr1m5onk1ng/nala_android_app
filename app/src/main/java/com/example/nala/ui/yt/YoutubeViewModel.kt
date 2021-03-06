package com.example.nala.ui.yt

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.domain.model.yt.*
import com.example.nala.network.model.yt.captions.CaptionsMapEntry
import com.example.nala.repository.DictionaryRepository
import com.example.nala.repository.YouTubeRepository
import com.example.nala.domain.model.utils.DataState
import com.example.nala.domain.model.utils.ErrorType
import com.example.nala.utils.utilities.ConnectionChecker
import com.example.nala.utils.Utils
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YoutubeViewModel @Inject constructor(
    private val youtubeRepository: YouTubeRepository,
    private val dictRepository: DictionaryRepository,
    private val networkChecker: ConnectionChecker,
) : ViewModel() {

    private var ytPlayer: YouTubePlayer? = null

    private var targetLangs = setOf<String>()

    private val _currentVideoId = MutableStateFlow("")

    private var authToken: String? = null

    val videoDataLoading = mutableStateOf(false)

    private val currentVideoUrl = mutableStateOf("")

    val currentVideoId: StateFlow<String> = _currentVideoId

    private var nextCommentsPageId: String? = null

    @ExperimentalCoroutinesApi
    private val videoCaptionsStateFlow = _currentVideoId.mapLatest {
        youtubeRepository.getVideoCaptions(videoId = it, lang = selectedCaptionTrack.value)
    }

    @ExperimentalCoroutinesApi
    private val videoCommentsStateFlow = _currentVideoId.mapLatest{ vId ->
        Log.d("AUTHDEBUG", "LOADING COMMENTS WIHT TOKEN: $authToken")
            youtubeRepository.getVideoComments(vId, authToken, nextCommentsPageId)
        }

    val captionsState: MutableState<DataState<List<YoutubeCaptionModel>>> =
        mutableStateOf(DataState.Initial(listOf()))

    val commentsState: MutableState<DataState<YoutubeCommentsList>> =
        mutableStateOf(DataState.Initial(YoutubeCommentsList.Empty()))

    val lastUpdatedComments = mutableStateOf<List<YoutubeCommentModel>>(listOf())

    val isUpdatingComments = mutableStateOf(false)

    val activeCaption: MutableState<Int> = mutableStateOf(0)

    val captionsMap: MutableState<Map<Int, List<CaptionsMapEntry>>> = mutableStateOf(mapOf())

    var currentPlayerPosition: Float = 0f

    val listState: MutableState<LazyListState> =
        mutableStateOf(LazyListState( firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0))

    val selectedTab: MutableState<Int> = mutableStateOf(0)

    private val _inspectedComment = MutableStateFlow<YoutubeCommentModel?>(null)

    val inspectedComment: StateFlow<YoutubeCommentModel?> = _inspectedComment

    private val _inspectedCaption = MutableStateFlow<YoutubeCaptionModel?>(null)

    val inspectedCaption: StateFlow<YoutubeCaptionModel?> = _inspectedCaption

    val inspectedElementTokens: MutableState<List<String>> = mutableStateOf(listOf())

    val inspectedElementTokensMap : MutableState<Map<Pair<Int, Int>, String>> = mutableStateOf(mapOf())

    val inspectedElementSelectedWord: MutableState<String> = mutableStateOf("")

    @ExperimentalCoroutinesApi
    private val inspectedCaptionTokensFlow = _inspectedCaption.mapLatest { caption ->
        caption?.caption?.let{
           dictRepository.tokenize(it)
        }
    }

    @ExperimentalCoroutinesApi
    private val inspectedCommentTokensFlow = _inspectedComment.mapLatest { comment ->
        comment?.content?.let{
            dictRepository.tokenize(it)
        }
    }

    private val _isVideoSaved = MutableStateFlow(false)

    val isVideoInFavorites = mutableStateOf(false)

    @ExperimentalCoroutinesApi
    private val isVideoSavedFlow = _isVideoSaved.flatMapLatest {
        youtubeRepository.getSavedVideo(currentVideoId.value).map {
            !it.isEmpty()
        }
    }

    val availableCaptionTracks: MutableState<List<YoutubeCaptionTracksModel>> =
        mutableStateOf(listOf())

    val selectedCaptionTrack = mutableStateOf("")

    @ExperimentalCoroutinesApi
    fun onLoadTrack(langCode: String) {
        viewModelScope.launch{
            selectedCaptionTrack.value = langCode
            loadCaptions()
        }
    }

    fun setSelectedWord(word: String) {
        inspectedElementSelectedWord.value = word
    }

    @ExperimentalCoroutinesApi
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

    @ExperimentalCoroutinesApi
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
        ytPlayer = player
    }

    fun setSelectedTab(index: Int) {
        selectedTab.value = index
    }

    @ExperimentalCoroutinesApi
    fun setVideoModel(url: String) {
        currentVideoUrl.value = url
        val videoId = Utils.parseVideoIdFromUrl(url)
        getVideoData(videoId)
        setIsVideoSaved()
    }

    @ExperimentalCoroutinesApi
    fun setVideoModelFromCache(video: YoutubeVideoModel) {
        getVideoData(video.id)
        setIsVideoSaved()
    }

    fun setPlayerPosition(position: Float) {
        ytPlayer!!.seekTo(position)
        ytPlayer!!.play()
    }

    @ExperimentalCoroutinesApi
    fun loadCaptions() = viewModelScope.launch{
        if(!networkChecker.isNetworkAvailable()) {
            captionsState.value = DataState.Error(ErrorType.NETWORK_NOT_AVAILABLE)
        } else {
            captionsState.value = DataState.Loading
            try{
                videoCaptionsStateFlow.collect {
                    captionsState.value = DataState.Success(it)
                    captionsMap.value = buildCaptionsMap(it)
                    //Log.d("YOUTUBEDEBUG", "Current captions: $it")
                }
            } catch(e: Exception){
                //Log.d("YOUTUBEDEBUG", "SOMETHING WENT WRONG: $e")
                captionsState.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun loadComments(accessToken: String?) = viewModelScope.launch{
        commentsState.value = DataState.Loading
        setAccessToken(accessToken)
        try{
            videoCommentsStateFlow.collect{
                nextCommentsPageId = it.nextPageToken
                val newData = ArrayList(lastUpdatedComments.value)
                newData.addAll(it.comments)
                lastUpdatedComments.value = newData
                commentsState.value = DataState.Success(
                    YoutubeCommentsList(
                        comments = newData,
                        nextPageToken = it.nextPageToken,
                    )
                )
            }
        } catch(e: Exception){
            Log.d("AUTHDEBUG", "Error Fetching Comments: $e")
            commentsState.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
        }
    }

    @ExperimentalCoroutinesApi
    fun updateComments() = viewModelScope.launch {
        isUpdatingComments.value = true
        try{
            videoCommentsStateFlow.collect{
                nextCommentsPageId = it.nextPageToken
                val newData = ArrayList(lastUpdatedComments.value)
                newData.addAll(it.comments)
                lastUpdatedComments.value = newData
                commentsState.value = DataState.Success(
                    YoutubeCommentsList(
                        comments = newData,
                        nextPageToken = it.nextPageToken,
                    )
                )
                isUpdatingComments.value = false
            }
        } catch(e: Exception){
            commentsState.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
        } finally {
            isUpdatingComments.value = false
        }
    }

    fun onPlayerTimeElapsed(secondsElapsed: Float){
        currentPlayerPosition = secondsElapsed
        val currentCaption = bucketSearch(secondsElapsed)
        currentCaption?.let{
            activeCaption.value = it.index
        }
    }

    /*
    fun onSeekTo(youTubePlayer: YouTubePlayer, caption: YoutubeCaptionModel) {
        caption.start?.let{
            youTubePlayer.seekTo(it)
        }
    } */

    @ExperimentalCoroutinesApi
    fun setIsVideoSaved()  {
        viewModelScope.launch {
            isVideoSavedFlow.collect{
                _isVideoSaved.value = it
                isVideoInFavorites.value = it
            }
        }
    }

    fun setVideoInFavorites(value: Boolean) {
        isVideoInFavorites.value = value
    }

    fun addVideoToFavorites() =
        viewModelScope.launch{
            youtubeRepository.addVideoToFavorites(currentVideoId.value, currentVideoUrl.value)
    }

    fun removeVideoFromFavorites() =
        viewModelScope.launch{
            youtubeRepository.removeVideoFromFavorites(currentVideoId.value)
        }

    fun checkNetworkAvailable() = networkChecker.isNetworkAvailable()

    fun onRetry() {
        ytPlayer?.loadVideo(currentVideoId.value, 0f)
    }

    fun setTargetLangs(langs: Set<String>) {
        targetLangs = langs
    }

    fun setPause() {
        ytPlayer?.pause()
    }

    fun seekTo(value: Float) {
        ytPlayer?.seekTo(value)
        ytPlayer?.play()
    }

    private fun setAccessToken(token: String?) {
        authToken = token
    }

    @ExperimentalCoroutinesApi
    private fun getVideoData(videoId: String) {
        viewModelScope.launch{
            videoDataLoading.value = true
            _currentVideoId.value = videoId
            //Log.d("YOUTUBEDEBUG", "Current Video Model: ${_currentVideoId.value}")
            val tracks = youtubeRepository.getVideoCaptionsTracks(videoId)
            /*val targetLangs =
                context.getSharedPreferences("langs", Context.MODE_PRIVATE)
                    .getStringSet("target_langs", setOf())?.toSet() ?: setOf() */
            val filteredTracks = tracks.filter{
                targetLangs.contains(it.langCode)
            }
            availableCaptionTracks.value = filteredTracks
            setIsVideoSaved()
            videoDataLoading.value = false
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
        //Log.d("YOUTUBEDEBUG", "Current player position: $currentPlayerPosition")
        //Log.d("YOUTUBEDEBUG", "Current map index: $index")
        val bucket = captionsMap.value[index]
        //Log.d("YOUTUBEDEBUG", "Current bucket: $bucket")
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