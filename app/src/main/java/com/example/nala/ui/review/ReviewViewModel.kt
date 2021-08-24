package com.example.nala.ui.review

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.db.models.review.KanjiReviewModel
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.metadata.MetadataModel
import com.example.nala.domain.model.review.ReviewCategory
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.repository.ReviewRepository
import com.example.nala.services.metadata.ExtractorService
import com.example.nala.domain.model.utils.DataState
import com.example.nala.domain.model.utils.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val metadataService: ExtractorService<MetadataModel>,
) : ViewModel() {

    val reviewsLoading: MutableState<Boolean> = mutableStateOf(false)
    val addedToReview: MutableState<Boolean> = mutableStateOf(false)
    val wordReviewItems: MutableState<DataState<List<WordReviewModel>>> =
        mutableStateOf(DataState.Initial(listOf()))
    val sentenceReviewItems: MutableState<DataState<List<SentenceReviewModel>>> =
        mutableStateOf(DataState.Initial(listOf()))
    val kanjiReviewItems: MutableState<DataState<List<KanjiReviewModel>>> =
        mutableStateOf(DataState.Initial(listOf()))

    val isArticleLoaded = mutableStateOf(false)

    val currentArticleUrl = mutableStateOf("")

    val isArticleSaved = mutableStateOf(false)

    private val isArticleSavedFlow =
        reviewRepository.getSavedArticle(currentArticleUrl.value).map {
            it.isNotEmpty()
        }

    init {
        loadWordReviewItems()
        loadSentenceReviewItems()
        loadKanjiReviewItems()
    }

    val selectedCategory: MutableState<ReviewCategory> = mutableStateOf(ReviewCategory.Word)

    fun setArticle(url: String) {
        viewModelScope.launch{
            isArticleLoaded.value = false
            currentArticleUrl.value = url
            Log.d("ARTICLESDEBUG", "Article: ${currentArticleUrl.value}")
            checkArticleSaved()
            isArticleLoaded.value = true
        }
    }

    fun setArticleFromCache(article: ArticlesCache) {
        isArticleLoaded.value = false
        currentArticleUrl.value = article.url
        isArticleLoaded.value = true
    }

    fun updateWordReviewItem(quality: Int, reviewModel: WordReviewModel) {
        viewModelScope.launch {
            reviewRepository.updateWordReviewParameters(quality, reviewModel)
        }
    }

    fun updateSentenceReviewItem(quality: Int, sentenceReviewModel: SentenceReviewModel) {
        viewModelScope.launch {
            reviewRepository.updateSentenceReviewParameters(quality, sentenceReviewModel)
        }
    }

    fun updateKanjiReviewItem(quality: Int, kanjiReviewModel: KanjiReviewModel) {
        viewModelScope.launch {
            reviewRepository.updateKanjiReviewParameters(quality, kanjiReviewModel)
        }
    }

    fun removeSentenceReviewItem(sentenceReview: SentenceReviewModel) {
        viewModelScope.launch{
            reviewRepository.removeSentenceReview(sentenceReview)
        }
    }

    fun removeWordReviewItem(wordReview: WordReviewModel) {
        viewModelScope.launch {
            reviewRepository.removeWordReview(wordReview)
        }
    }

    fun removeKanjiReviewItem(kanjiReview: KanjiReviewModel) {
        viewModelScope.launch {
            reviewRepository.removeKanjiReviewItem(kanjiReview)
        }
    }

    fun loadWordReviewItems() {
        viewModelScope.launch {
            wordReviewItems.value = DataState.Loading
            reviewRepository.getNWordReviews(30).collect{
                if(it.isEmpty()) {
                    wordReviewItems.value = DataState.Error(ErrorType.DATA_NOT_AVAILABLE)
                } else {
                    wordReviewItems.value = DataState.Success(it)
                }
            }
        }
    }

    fun loadSentenceReviewItems() {
        viewModelScope.launch {
            sentenceReviewItems.value = DataState.Loading
            reviewRepository.getNSentenceReviewItems(30).collect{
                if(it.isEmpty()) {
                    sentenceReviewItems.value = DataState.Error(ErrorType.DATA_NOT_AVAILABLE)
                } else {
                    sentenceReviewItems.value = DataState.Success(it)
                }
            }
        }
    }

    fun loadKanjiReviewItems() {
        viewModelScope.launch {
            kanjiReviewItems.value = DataState.Loading
            reviewsLoading.value = true
            reviewRepository.getNKanjiReviewItems(30).collect {
                if(it.isEmpty()) {
                    kanjiReviewItems.value = DataState.Error(ErrorType.DATA_NOT_AVAILABLE)
                } else {
                    kanjiReviewItems.value = DataState.Success(it)
                }
            }
        }

    }

    fun setCategory (category: ReviewCategory) {
        selectedCategory.value = category
    }

    private fun checkArticleSaved()  {
        viewModelScope.launch {
            isArticleSavedFlow.collect{
                isArticleSaved.value = it
            }
        }
    }
}