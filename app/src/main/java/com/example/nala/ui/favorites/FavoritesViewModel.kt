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

    val savedVideosState: MutableState<DataState<List<YoutubeVideoModel>>> =
        mutableStateOf(DataState.Initial(listOf()))

    val savedArticlesState: MutableState<DataState<List<ArticlesCache>>> =
        mutableStateOf(DataState.Initial(listOf()))

    private val savedVideosFlow =
        youtubeRepository.getSavedVideos()

    private val savedArticlesFlow =
        reviewRepository.getSavedArticles()

    fun addVideoToFavorites(videoId: String, videoUrl: String) =
        viewModelScope.launch{
            youtubeRepository.addVideoToFavorites(videoId, videoUrl)
        }

    fun loadSavedArticles() = viewModelScope.launch(Dispatchers.IO) {
        savedArticlesState.value = DataState.Loading
        try{
            savedArticlesFlow.collect{
                savedArticlesState.value = DataState.Success(it)
            }
        } catch (e: Exception) {
            savedArticlesState.value = DataState.Error(ErrorType.DATA_NOT_AVAILABLE)
        }
    }

    fun loadSavedVideos() = viewModelScope.launch(Dispatchers.IO) {
        savedVideosState.value = DataState.Loading
        try{
            savedVideosFlow.collect{
                savedVideosState.value = DataState.Success(it)
            }
        } catch (e: Exception) {
            savedVideosState.value = DataState.Error(ErrorType.DATA_NOT_AVAILABLE)
        }
    }

    fun removeVideoFromFavorites(videoId: String) =
        viewModelScope.launch{
            youtubeRepository.removeVideoFromFavorites(videoId)
        }

    fun addArticleToFavorites(article: ArticlesCache) =
        viewModelScope.launch {
            reviewRepository.addArticleToFavorites(article)
        }

    fun removeArticleFromFavorites(url: String) =
        viewModelScope.launch {
            reviewRepository.removeArticleFromFavorites(url)
        }
}