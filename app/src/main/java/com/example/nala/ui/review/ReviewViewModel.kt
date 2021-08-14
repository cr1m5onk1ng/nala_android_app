package com.example.nala.ui.review

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.db.models.review.KanjiReviewModel
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.review.ReviewCategory
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.repository.ReviewRepository
import com.example.nala.service.metadata.ExtractorService
import com.example.nala.ui.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val metadataService: ExtractorService,
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

    val currentArticle = mutableStateOf(ArticlesCache.Empty())

    val isArticleSaved = mutableStateOf(false)

    private val isArticleSavedFlow =
        reviewRepository.getSavedArticle(currentArticle.value.url).map {
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
            withContext(Dispatchers.IO) {
                isArticleLoaded.value = false
                val metadata = metadataService.extractFromUrl(url)
                currentArticle.value = ArticlesCache(
                    url = url,
                    title = metadata.title,
                    description = metadata.description,
                    thumbnailUrl = metadata.thumbnailUrl
                )
                Log.d("ARTICLESDEBUG", "Article: ${currentArticle.value}")
                checkArticleSaved()
                isArticleLoaded.value = true
            }
        }
    }

    fun setArticleFromCache(article: ArticlesCache) {
        isArticleLoaded.value = false
        currentArticle.value = article
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
                    wordReviewItems.value = DataState.Error("No review items present")
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
                    sentenceReviewItems.value = DataState.Error("No review items present")
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
                    kanjiReviewItems.value = DataState.Error("No review items present")
                } else {
                    kanjiReviewItems.value = DataState.Success(it)
                }
            }
        }

    }

    fun setCategory (category: ReviewCategory) {
        selectedCategory.value = category
    }

    /*
    fun saveArticle(url: String) {
        viewModelScope.launch{
            reviewRepository.addArticleToFavorites(url)
        }
    } */

    private fun checkArticleSaved()  {
        viewModelScope.launch {
            isArticleSavedFlow.collect{
                isArticleSaved.value = it
            }
        }
    }
}