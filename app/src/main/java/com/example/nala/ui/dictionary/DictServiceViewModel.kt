package com.example.nala.ui.dictionary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.repository.DictionaryRepository
import com.example.nala.repository.KanjiRepository
import com.example.nala.repository.ReviewRepository
import com.example.nala.domain.model.utils.DataState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class DictServiceViewModel @Inject constructor(
    private val reviewRepo: ReviewRepository,
    private val dictRepo: DictionaryRepository,
    private val kanjiRepo: KanjiRepository,
) : ViewModel() {

    init{
        getWordsInReview()
        getKanjisInReview()
    }

    private val _currentWordState =
        MutableStateFlow<DataState<DictionaryModel>>(DataState.Initial(DictionaryModel.Empty()))


    val currentWord: StateFlow<DataState<DictionaryModel>> = _currentWordState

    private val _currentWordKanjis = MutableStateFlow<List<KanjiModel>>(listOf())


    private val currentKanjisFlow = _currentWordKanjis.flatMapLatest {
        flow<List<KanjiModel>>{kanjiRepo.getWordKanjis(selectedWord.value)}
    }

    val currentWordKanjis: StateFlow<List<KanjiModel>> = _currentWordKanjis

    private val _isKanjiInReviewArray = MutableStateFlow<List<Boolean>>(listOf())

    val isKanjiInReviewArray: StateFlow<List<Boolean>> = _isKanjiInReviewArray

    private val _selectedWord = MutableStateFlow("")

    val selectedWord: StateFlow<String> = _selectedWord

    private val searchResultsFlow =
        _selectedWord.flatMapLatest { query ->
            dictRepo.searchFlow(query)
        }

    private val _wordReviewsState = MutableStateFlow<List<String>>(listOf())

    private val wordReviewsFlow = _wordReviewsState.flatMapLatest{
        reviewRepo.getWordReviews().mapLatest { reviews ->
            reviews.map{ it.word }
        }
    }

    val wordRevies: StateFlow<List<String>> = _wordReviewsState

    private val _kanjiReviewsState = MutableStateFlow<List<String>>(listOf())

    private val kanjiReviewsFlow = _kanjiReviewsState.flatMapLatest{
        reviewRepo.getAllKanjiReviewItems().mapLatest { reviews ->
            reviews.map{ it.kanji }
        }
    }

    val kanjiReviews: StateFlow<List<String>> = _kanjiReviewsState

    private val _isWordInReview = MutableStateFlow<Boolean>(false)

    val isWordInReview: StateFlow<Boolean> = _isWordInReview

    private val _kanjiStoriesState = MutableStateFlow<List<String>>(listOf())

    val kanjiStories: StateFlow<List<String>> = _kanjiStoriesState

    fun searchWord(word: String) {
        viewModelScope.launch{
            _selectedWord.value = word
            _currentWordState.value = DataState.Loading
            searchResultsFlow.collect{
                _currentWordState.value = DataState.Success(it)
                getWordKanjis()
                Log.d("DICTIONARYWINDOW", "current word: ${currentWord.value}")
            }
        }
    }

    fun addWordToFavorites(word: DictionaryModel) {
        viewModelScope.launch{
            reviewRepo.addWordToReview(word)
        }
    }

    fun addKanjiToFavorites(kanji: KanjiModel) {
        viewModelScope.launch{
            reviewRepo.addKanjiToReview(kanji)
        }
    }

    private fun getWordsInReview() {
        viewModelScope.launch {
            wordReviewsFlow.collect{ reviews ->
                if(reviews.contains(selectedWord.value)) {
                    _isWordInReview.value = true
                }
                _wordReviewsState.value = reviews
            }
        }
    }

    private fun getKanjisInReview() {
        viewModelScope.launch{
            kanjiReviewsFlow.collect{
                _kanjiReviewsState.value = it
            }
        }
    }

    private suspend fun getWordKanjis() {
        currentKanjisFlow.collect{
            val kanjisInReview = mutableListOf<Boolean>()
            val kanjiStories = mutableListOf<String>()
            it.forEach { kanji ->
                if(kanjiReviews.value.contains(kanji.kanji)){
                    kanjisInReview.add(true)
                } else {
                    kanjisInReview.add(false)
                }
                val story = kanjiRepo.getKanjiStory(kanji.kanji)
                kanjiStories.add(story)
            }
            _isKanjiInReviewArray.value = kanjisInReview
            _currentWordKanjis.value = it
            _kanjiStoriesState.value = kanjiStories
            Log.d("DICTIONARYWINDOW", "Kanji in review array: ${isKanjiInReviewArray.value}")
            Log.d("DICTIONARYWINDOW", "Word kanjis: ${currentWordKanjis.value}")
        }
    }

}