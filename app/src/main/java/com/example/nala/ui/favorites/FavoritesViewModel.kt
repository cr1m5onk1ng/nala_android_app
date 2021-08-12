package com.example.nala.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.domain.model.yt.YoutubeVideoModel
import com.example.nala.repository.ReviewRepository
import com.example.nala.repository.YouTubeRepository
import com.example.nala.ui.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val youtubeRepository: YouTubeRepository,
) : ViewModel() {

    private val _savedVideosState =
        MutableStateFlow<DataState<List<YoutubeVideoModel>>>(DataState.Initial(listOf()))

    val savedVideoState: StateFlow<DataState<List<YoutubeVideoModel>>> = _savedVideosState

    private val _savedArticlesState =
        MutableStateFlow<DataState<List<ArticlesCache>>>(DataState.Initial(listOf()))

    val savedArticlesState: StateFlow<DataState<List<ArticlesCache>>> = _savedArticlesState

    private val savedVideosFlow = _savedVideosState.flatMapLatest {
        youtubeRepository.getSavedVideos()
    }

    private val savedArticlesFlow = _savedArticlesState.flatMapLatest {
        reviewRepository.getSavedArticles()
    }

    fun addVideoToFavorites(video: YoutubeVideoModel) =
        viewModelScope.launch{
            youtubeRepository.addVideoToFavorites(video)
    }

    fun loadSavedArticles() = viewModelScope.launch {
        _savedArticlesState.value = DataState.Loading
        try{
            savedArticlesFlow.collect{
                _savedArticlesState.value = DataState.Success(it)
            }
        } catch (e: Exception) {
            _savedArticlesState.value = DataState.Error("Couldnt fetch articles: $e")
        }
    }

    fun loadSavedVideos() = viewModelScope.launch {
        _savedVideosState.value = DataState.Loading
        try{
            savedVideosFlow.collect{
                _savedVideosState.value = DataState.Success(it)
            }
        } catch (e: Exception) {
            _savedVideosState.value = DataState.Error("Couldnt fetch videos: $e")
        }
    }

    fun removeVideoFromFavorites(videoId: String) =
        viewModelScope.launch{
            youtubeRepository.removeVideoFromFavorites(videoId)
        }

    fun addArticleToFavorites(url: String) =
        viewModelScope.launch {
            reviewRepository.addArticleToFavorites(url)
        }

    fun removeArticleFromFavorites(url: String) =
        viewModelScope.launch {
            reviewRepository.removeArticleFromFavorites(url)
        }
}