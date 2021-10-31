package com.example.nala.ui.review

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.ArticlesCache
import com.example.nala.db.models.review.KanjiReviewCache
import com.example.nala.db.models.review.SentenceReviewCache
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.repository.ReviewRepository
import com.example.nala.domain.model.utils.DataState
import com.example.nala.domain.model.utils.ErrorType
import com.example.nala.utils.utilities.KanjiReviewsPager
import com.example.nala.utils.utilities.SentenceReviewsPager
import com.example.nala.utils.utilities.WordReviewsPager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val wordReviewsPager: WordReviewsPager,
    private val sentenceReviewsPager: SentenceReviewsPager,
    private val kanjiReviewsPager: KanjiReviewsPager,
) : ViewModel() {

    // WORD REVIEWS STATE VARIABLES
    private val _wordReviewItemsStateFlow =
        MutableStateFlow<DataState<List<WordReviewModel>>>(DataState.Initial(listOf()))
    val wordReviewItemsState: StateFlow<DataState<List<WordReviewModel>>>
        get() = _wordReviewItemsStateFlow.asStateFlow()
    private var currentWordItems = mutableListOf<WordReviewModel>()
    val wordsEndReached = mutableStateOf(false)
    val wordsListState = LazyListState()


    // SENTENCE REVIEWS STATE VARIABLES
    private val _sentenceReviewItemsStateFlow =
        MutableStateFlow<DataState<List<SentenceReviewModel>>>(DataState.Initial(listOf()))
    val sentenceReviewItemsState: StateFlow<DataState<List<SentenceReviewModel>>>
        get() = _sentenceReviewItemsStateFlow.asStateFlow()
    private var currentSentenceItems = mutableListOf<SentenceReviewCache>()
    val sentencesEndReached = mutableStateOf(false)
    val sentencesListState = LazyListState()


    // KANJI REVIEWS STATE VARIABLES
    private val _kanjiReviewItemsStateFlow =
        MutableStateFlow<DataState<List<KanjiReviewCache>>>(DataState.Initial(listOf()))
    val kanjiReviewItemsState: StateFlow<DataState<List<KanjiReviewCache>>>
        get() = _kanjiReviewItemsStateFlow.asStateFlow()
    private var currentKanjiItems = mutableListOf<KanjiReviewCache>()
    val kanjisEndReached = mutableStateOf(false)
    val kanjisListState = LazyListState()

    val selectedTab = mutableStateOf(0)

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
            when(selectedTab.value) {
                0 -> {
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
                1 -> {
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
                2 -> {
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
        viewModelScope.launch(Dispatchers.IO){
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
        viewModelScope.launch(Dispatchers.IO){
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
                    if(currentWordItems.isEmpty()) {
                        _wordReviewItemsStateFlow.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
                    } else {
                        _wordReviewItemsStateFlow.value = DataState.Success(currentWordItems)
                    }
                }
            } catch(e: Exception) {
                _wordReviewItemsStateFlow.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
            }
        }
    }


    fun loadSentenceReviewItemsFlow() {
        viewModelScope.launch(Dispatchers.IO){
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

    fun loadPagedSentenceReviewItems() {
        viewModelScope.launch(Dispatchers.IO){
            _sentenceReviewItemsStateFlow.value = DataState.Loading
            delay(1000)
            try {
                sentenceReviewsPager.getNextResult().collect{
                    Log.d("PAGINATIONDEBUG", "Current Results: $it")
                    val data = it.data
                    if(it.hasReachedEnd){
                        Log.d("PAGINATIONDEBUG", "End Of Results Reached")
                        sentencesEndReached.value = true
                    }
                    currentSentenceItems.addAll(data)
                    if(currentSentenceItems.isEmpty()) {
                        _sentenceReviewItemsStateFlow.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
                    } else {
                        _sentenceReviewItemsStateFlow.value = DataState.Success(
                            currentSentenceItems.map{ sent ->
                                SentenceReviewModel(
                                    sentence = sent.sentence,
                                    targetWord = sent.targetWord,
                                )
                            }
                        )
                    }
                }
            } catch(e: Exception) {
                _sentenceReviewItemsStateFlow.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
            }
        }
    }

    fun loadKanjiReviewItemsFlow(){
        viewModelScope.launch(Dispatchers.IO){
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

    fun loadPagedKanjiReviewItems() {
        viewModelScope.launch(Dispatchers.IO){
            _kanjiReviewItemsStateFlow.value = DataState.Loading
            try {
                kanjiReviewsPager.getNextResult().collect{
                    Log.d("PAGINATIONDEBUG", "Current Results: $it")
                    val data = it.data
                    if(it.hasReachedEnd){
                        Log.d("PAGINATIONDEBUG", "End Of Results Reached")
                        kanjisEndReached.value = true
                    }
                    currentKanjiItems.addAll(data)
                    if(currentKanjiItems.isEmpty()) {
                        _kanjiReviewItemsStateFlow.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
                    }else {
                        _kanjiReviewItemsStateFlow.value = DataState.Success(currentKanjiItems)
                    }
                }
            } catch(e: Exception) {
                _kanjiReviewItemsStateFlow.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
            }
        }
    }

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

    fun setTab(tab: Int) {
        selectedTab.value = tab
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
        when(selectedTab.value) {
            0 -> {
                loadWordReviewItemsFlow()
            }
            1 -> {
                loadSentenceReviewItemsFlow()
            }
            2 -> {
                loadKanjiReviewItemsFlow()
            }
        }
    }

    private fun restoreWordItems() {
        if(currentWordItems.isNotEmpty()) {
            _wordReviewItemsStateFlow.value = DataState.Success(currentWordItems)
        } else {
            _wordReviewItemsStateFlow.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
        }

    }

    private fun restoreSentenceItems() {
        if(currentWordItems.isNotEmpty()) {
            _sentenceReviewItemsStateFlow.value = DataState.Success(currentSentenceItems.map{
                SentenceReviewModel(
                    sentence = it.sentence,
                    targetWord = it.targetWord
                )
            })
        } else {
            _sentenceReviewItemsStateFlow.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
        }
    }

    private fun restoreKanjiItems() {
        if(currentWordItems.isNotEmpty()) {
            _kanjiReviewItemsStateFlow.value = DataState.Success(currentKanjiItems)
        } else {
            _kanjiReviewItemsStateFlow.value = DataState.Error(ErrorType.ERROR_FETCHING_DATA)
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