package com.example.nala.ui.review

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.db.models.review.KanjiReviewCache
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.review.ReviewCategory
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.repository.ReviewRepository
import com.example.nala.domain.model.utils.DataState
import com.example.nala.domain.model.utils.ErrorType
import com.example.nala.utils.utilities.WordReviewsPager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val wordReviewsPager: WordReviewsPager,
) : ViewModel() {

    // WORD REVIEWS STATE VARIABLES
    private val _wordReviewItemsStateFlow =
        MutableStateFlow<DataState<List<WordReviewModel>>>(DataState.Initial(listOf()))
    val wordReviewItemsState: StateFlow<DataState<List<WordReviewModel>>>
        get() = _wordReviewItemsStateFlow.asStateFlow()
    private var currentWordItems = mutableListOf<WordReviewModel>()
    val loadingNextWordReviews = mutableStateOf(false)
    val wordsEndReached = mutableStateOf(false)


    // SENTENCE REVIEWS STATE VARIABLES
    private val _sentenceReviewItemsStateFlow =
        MutableStateFlow<DataState<List<SentenceReviewModel>>>(DataState.Initial(listOf()))
    val sentenceReviewItemsState: StateFlow<DataState<List<SentenceReviewModel>>>
        get() = _sentenceReviewItemsStateFlow.asStateFlow()


    // KANJI REVIEWS STATE VARIABLES
    private val _kanjiReviewItemsStateFlow =
        MutableStateFlow<DataState<List<KanjiReviewCache>>>(DataState.Initial(listOf()))
    val kanjiReviewItemsState: StateFlow<DataState<List<KanjiReviewCache>>>
        get() = _kanjiReviewItemsStateFlow.asStateFlow()

    /*private val _selectedCategoryStateFlow =
        MutableStateFlow(ReviewCategory.Word)
    val selectedCategoryState: StateFlow<ReviewCategory>
        get() = _selectedCategoryStateFlow.asStateFlow()*/

    val selectedCategory: MutableState<ReviewCategory> = mutableStateOf(ReviewCategory.Word)

    private val _searchQueryStateFlow = MutableStateFlow("")
    //val searchQueryState = _searchQueryStateFlow.asStateFlow()

    private val wordLoadingStateFlow =
        _searchQueryStateFlow.flatMapLatest{
            flow<List<WordReviewModel>>{reviewRepository.getMatchingWords(it)}
        }

    private val sentenceLoadingStateFlow =
        _searchQueryStateFlow.flatMapLatest {
            flow<List<SentenceReviewModel>>{reviewRepository.getMatchingSentences(it)}
        }

    private val kanjiLoadingStateFlow =
        _searchQueryStateFlow.flatMapLatest {
            flow<KanjiReviewCache>{reviewRepository.getKanjiReviewItem(it)}
        }

    val addedToReview: MutableState<Boolean> = mutableStateOf(false)

    private val wordReviewItemsCache: MutableState<List<WordReviewModel>> = mutableStateOf(listOf())

    private val sentenceReviewItemsCache: MutableState<List<SentenceReviewModel>> = mutableStateOf(listOf())

    private val kanjiReviewItemsCache: MutableState<List<KanjiReviewCache>> = mutableStateOf(listOf())

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

    fun searchFlow(inputQuery: String) {
        viewModelScope.launch{
            _searchQueryStateFlow.value = inputQuery
            when(selectedCategory.value) {
                ReviewCategory.Word -> {
                    _wordReviewItemsStateFlow.value = DataState.Loading
                    wordLoadingStateFlow.collect{ result ->
                        Log.d("SEARCHDEBUG", "Search result is: $result")
                        if(result.isEmpty()) {
                            _wordReviewItemsStateFlow.value = DataState.Error(ErrorType.NO_SEARCH_RESULTS)
                        } else {
                            _wordReviewItemsStateFlow.value = DataState.Success(result)
                        }
                    }
                }
                ReviewCategory.Sentence -> {
                    _sentenceReviewItemsStateFlow.value = DataState.Loading
                    sentenceLoadingStateFlow.collect{ matches ->
                        if(matches.isEmpty()) {
                            _sentenceReviewItemsStateFlow.value = DataState.Error(ErrorType.NO_SEARCH_RESULTS)
                        } else {
                            _sentenceReviewItemsStateFlow.value =
                                DataState.Success(matches)
                        }
                    }
                }
                ReviewCategory.Kanji -> {
                    _kanjiReviewItemsStateFlow.value = DataState.Loading
                    kanjiLoadingStateFlow.collect{ matches ->
                        if(matches.kanji == "") {
                            _kanjiReviewItemsStateFlow.value = DataState.Error(ErrorType.NO_SEARCH_RESULTS)
                        } else {
                            _kanjiReviewItemsStateFlow.value =
                                DataState.Success(listOf(matches))
                        }
                    }
                }
            }
        }
    }

    fun loadWordReviewItemsFlow() {
        viewModelScope.launch{
            _wordReviewItemsStateFlow.value = DataState.Loading
            reviewRepository.getWordReviews().collect{
                if(it.isEmpty()) {
                    _wordReviewItemsStateFlow.value = DataState.Error(ErrorType.DATA_NOT_AVAILABLE)
                } else {
                    _wordReviewItemsStateFlow.value = DataState.Success(it)
                }
            }
        }
    }

    fun loadPagedWordReviewItems() {
        viewModelScope.launch{
            _wordReviewItemsStateFlow.value = DataState.Loading
            delay(1000)
            try {
                wordReviewsPager.getNextResult().collect{
                    Log.d("PAGINATIONDEBUG", "Current Results: $it")
                    val data = it.data
                    if(it.hasReachedEnd){
                        Log.d("PAGINATIONDEBUG", "End Of Results Reached")
                        wordsEndReached.value = true
                    }
                    currentWordItems.addAll(data)
                    _wordReviewItemsStateFlow.value = DataState.Success(currentWordItems)
                }
            } catch(e: Exception) {
                _wordReviewItemsStateFlow.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
            }
        }
    }


    fun loadSentenceReviewItemsFlow() {
        viewModelScope.launch{
            _sentenceReviewItemsStateFlow.value = DataState.Loading
            reviewRepository.getAllSentenceReviewItems().collect{
                if(it.isEmpty()) {
                    _sentenceReviewItemsStateFlow.value = DataState.Error(ErrorType.DATA_NOT_AVAILABLE)
                } else {
                    _sentenceReviewItemsStateFlow.value = DataState.Success(it)
                }
            }
        }
    }

    fun loadKanjiReviewItemsFlow(){
        viewModelScope.launch{
            _kanjiReviewItemsStateFlow.value = DataState.Loading
            reviewRepository.getAllKanjiReviewItems().collect{
                if(it.isEmpty()) {
                    _kanjiReviewItemsStateFlow.value = DataState.Error(ErrorType.DATA_NOT_AVAILABLE)
                } else {
                    _kanjiReviewItemsStateFlow.value = DataState.Success(it)
                }
            }
        }
    }

    // OLD METHODS

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

    fun updateKanjiReviewItem(quality: Int, kanjiReviewCache: KanjiReviewCache) {
        viewModelScope.launch {
            reviewRepository.updateKanjiReviewParameters(quality, kanjiReviewCache)
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

    fun removeWordFromString(word: String) {
        viewModelScope.launch{
            reviewRepository.removeWordReviewFromId(word)
        }
    }

    fun removeKanjiReviewItem(kanjiReview: KanjiReviewCache) {
        viewModelScope.launch {
            reviewRepository.removeKanjiReviewItem(kanjiReview)
        }
    }

    fun setCategory (category: ReviewCategory) {
        selectedCategory.value = category
        when(selectedCategory.value) {
            ReviewCategory.Word -> {
                //loadWordReviewItemsFlow()
                loadPagedWordReviewItems()
            }
            ReviewCategory.Sentence -> {
                loadSentenceReviewItemsFlow()
            }
            ReviewCategory.Kanji -> {
                loadKanjiReviewItemsFlow()
            }
        }
    }

    fun restoreWordFromReview(wordReview: WordReviewModel) =
        viewModelScope.launch{
            reviewRepository.restoreRemovedWordToReview(wordReview)
        }

    fun restoreSentenceFromReview(sentenceReviewModel: SentenceReviewModel) =
        viewModelScope.launch{
            reviewRepository.addSentenceToReview(sentenceReviewModel)
        }

    fun restoreKanjiFromReview(kanjiReviewCache: KanjiReviewCache) =
        viewModelScope.launch{
            reviewRepository.restoreKanjiToReview(kanjiReviewCache)
        }

    // ARTICLES LOGIC

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

    private fun restoreWordItems() {
        _wordReviewItemsStateFlow.value = DataState.Success(wordReviewItemsCache.value)
    }

    private fun restoreSentenceItems() {
        _sentenceReviewItemsStateFlow.value = DataState.Success(sentenceReviewItemsCache.value)
    }

    private fun restoreKanjiItems() {
        _kanjiReviewItemsStateFlow.value = DataState.Success(kanjiReviewItemsCache.value)
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