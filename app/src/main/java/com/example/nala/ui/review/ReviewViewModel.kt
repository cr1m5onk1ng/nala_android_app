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
import com.example.nala.domain.model.utils.DataState
import com.example.nala.domain.model.utils.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    val addedToReview: MutableState<Boolean> = mutableStateOf(false)

    val wordReviewItems: MutableState<DataState<List<WordReviewModel>>> =
        mutableStateOf(DataState.Initial(listOf()))
    private val wordReviewItemsCache: MutableState<List<WordReviewModel>> = mutableStateOf(listOf())

    val sentenceReviewItems: MutableState<DataState<List<SentenceReviewModel>>> =
        mutableStateOf(DataState.Initial(listOf()))
    private val sentenceReviewItemsCache: MutableState<List<SentenceReviewModel>> = mutableStateOf(listOf())

    val kanjiReviewItems: MutableState<DataState<List<KanjiReviewModel>>> =
        mutableStateOf(DataState.Initial(listOf()))
    private val kanjiReviewItemsCache: MutableState<List<KanjiReviewModel>> = mutableStateOf(listOf())

    val isArticleLoaded = mutableStateOf(false)

    val currentArticleUrl = mutableStateOf("")

    private val _isArticleSaved = MutableStateFlow(false)
    val isArticleSaved = mutableStateOf(false)

    @ExperimentalCoroutinesApi
    private val isArticleSavedFlow = _isArticleSaved.flatMapLatest{
        reviewRepository.getSavedArticle(currentArticleUrl.value).map {
            !it.isEmpty()
        }
    }

    init {
        loadWordReviewItems()
    }

    val selectedCategory: MutableState<ReviewCategory> = mutableStateOf(ReviewCategory.Word)

    @ExperimentalCoroutinesApi
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
                    wordReviewItemsCache.value = it
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
                    sentenceReviewItemsCache.value = it
                }
            }
        }
    }

    fun loadKanjiReviewItems() {
        viewModelScope.launch {
            kanjiReviewItems.value = DataState.Loading
            reviewRepository.getNKanjiReviewItems(30).collect {
                if(it.isEmpty()) {
                    kanjiReviewItems.value = DataState.Error(ErrorType.DATA_NOT_AVAILABLE)
                } else {
                    kanjiReviewItems.value = DataState.Success(it)
                    kanjiReviewItemsCache.value = it
                }
            }
        }
    }

    fun setCategory (category: ReviewCategory) {
        selectedCategory.value = category
        when(selectedCategory.value) {
            ReviewCategory.Word -> {
                loadWordReviewItems()
            }
            ReviewCategory.Sentence -> {
                loadSentenceReviewItems()
            }
            ReviewCategory.Kanji -> {
                loadKanjiReviewItems()
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch{
            when(selectedCategory.value) {
                ReviewCategory.Word -> {
                    wordReviewItems.value = DataState.Loading
                    val result = reviewRepository.getMatchingWords(query)
                    if(result.isEmpty()) {
                        wordReviewItems.value = DataState.Error(ErrorType.NO_SEARCH_RESULTS)
                    } else {
                        wordReviewItems.value = DataState.Success(result)
                    }
                }
                ReviewCategory.Sentence -> {
                    sentenceReviewItems.value = DataState.Loading
                    val matches = reviewRepository.getMatchingSentences(query)
                    if(matches.isEmpty()) {
                        sentenceReviewItems.value = DataState.Error(ErrorType.NO_SEARCH_RESULTS)
                    } else {
                        sentenceReviewItems.value =
                            DataState.Success(matches)
                    }
                }
                ReviewCategory.Kanji -> {
                    kanjiReviewItems.value = DataState.Loading
                    val matches = reviewRepository.getKanjiReviewItem(query)
                    if(matches.kanji == "") {
                        kanjiReviewItems.value = DataState.Error(ErrorType.NO_SEARCH_RESULTS)
                    } else {
                        kanjiReviewItems.value =
                            DataState.Success(listOf(matches))
                    }
                }
            }
        }
    }

    private fun restoreWordItems() {
        wordReviewItems.value = DataState.Success(wordReviewItemsCache.value)
    }

    private fun restoreSentenceItems() {
        sentenceReviewItems.value = DataState.Success(sentenceReviewItemsCache.value)
    }

    private fun restoreKanjiItems() {
        kanjiReviewItems.value = DataState.Success(kanjiReviewItemsCache.value)
    }

    fun restore() {
        when(selectedCategory.value) {
            ReviewCategory.Word -> {
                restoreWordItems()
            }
            ReviewCategory.Sentence -> {
                restoreSentenceItems()
            }
            ReviewCategory.Kanji -> {
                restoreKanjiItems()
            }
        }
    }

    fun setIsArticleInFavorites(value : Boolean) {
        isArticleSaved.value = value
    }

    fun addArticleToFavorites() {
        viewModelScope.launch{
            reviewRepository.addArticleToFavorites(currentArticleUrl.value)
        }
    }

    fun removeArticleFromFavorites() {
        viewModelScope.launch{
            reviewRepository.removeArticleFromFavorites(currentArticleUrl.value)
        }
    }

    @ExperimentalCoroutinesApi
    private fun checkArticleSaved()  {
        viewModelScope.launch {
            isArticleSavedFlow.collect{
                _isArticleSaved.value = it
                isArticleSaved.value = it
            }
        }
    }
}