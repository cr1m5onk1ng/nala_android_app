package com.example.nala.ui.favorites

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.domain.model.yt.YoutubeVideoModel
import com.example.nala.repository.ReviewRepository
import com.example.nala.repository.YouTubeRepository
import com.example.nala.domain.model.utils.DataState
import com.example.nala.domain.model.utils.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val youtubeRepository: YouTubeRepository,
) : ViewModel() {

    /*val savedVideosState: MutableState<DataState<List<YoutubeVideoModel>>> =
        mutableStateOf(DataState.Initial(listOf()))

    val savedArticlesState: MutableState<DataState<List<ArticlesCache>>> =
        mutableStateOf(DataState.Initial(listOf())) */

    private val _savedVideoStateFlow =
        MutableStateFlow<DataState<List<YoutubeVideoModel>>>(DataState.Initial(listOf()))

    private val _savedArticlesStateFlow =
        MutableStateFlow<DataState<List<ArticlesCache>>>(DataState.Initial(listOf()))

    val savedVideosState: StateFlow<DataState<List<YoutubeVideoModel>>>
        get() = _savedVideoStateFlow.asStateFlow()

    val savedArticlesState: StateFlow<DataState<List<ArticlesCache>>>
        get() = _savedArticlesStateFlow.asStateFlow()

    private val savedVideosFlow =
        _savedVideoStateFlow.flatMapLatest {
            youtubeRepository.getSavedVideos()
        }

    private val savedArticlesFlow =
        _savedArticlesStateFlow.flatMapLatest {
            reviewRepository.getSavedArticles()
        }

    fun restoreVideo(video: YoutubeVideoModel) =
        viewModelScope.launch{
            youtubeRepository.restoreVideo(video)
        }

    fun loadSavedArticles() = viewModelScope.launch(Dispatchers.IO) {
        _savedArticlesStateFlow.value = DataState.Loading
        try{
            savedArticlesFlow.collect{
                _savedArticlesStateFlow.value = DataState.Success(it)
            }
        } catch (e: Exception) {
            _savedArticlesStateFlow.value = DataState.Error(ErrorType.DATA_NOT_AVAILABLE)
        }
    }

    fun loadSavedVideos() = viewModelScope.launch(Dispatchers.IO) {
        _savedVideoStateFlow.value = DataState.Loading
        try{
            savedVideosFlow.collect{
                _savedVideoStateFlow.value = DataState.Success(it)
            }
        } catch (e: Exception) {
            _savedVideoStateFlow.value = DataState.Error(ErrorType.DATA_NOT_AVAILABLE)
        }
    }

    fun removeVideoFromFavorites(videoUrl: String) =
        viewModelScope.launch{
            youtubeRepository.removeVideoFromFavorites(videoUrl)
        }

    fun restoreArticle(article: ArticlesCache) =
        viewModelScope.launch {
            reviewRepository.restoreArticle(article)
        }

    fun removeArticleFromFavorites(url: String) =
        viewModelScope.launch {
            reviewRepository.removeArticleFromFavorites(url)
        }
}