package com.example.nala.ui.dictionary

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.WordReviewModelDto
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.kanji.KanjiCollection
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.kanji.StoriesCollection
import com.example.nala.repository.DictionaryRepository
import com.example.nala.repository.KanjiRepository
import com.example.nala.repository.ReviewRepository
import com.example.nala.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val dictRepository: DictionaryRepository,
    private val kanjiRepository: KanjiRepository,
    private val reviewRepository: ReviewRepository,
    @ApplicationContext appContext: Context,
) : ViewModel() {

    // HOME SCREEN STATE
    val textReceived: MutableState<Boolean> = mutableStateOf(false)

    val sentenceReceived: MutableState<Boolean> = mutableStateOf(false)

    val currentWordModel: MutableState<DictionaryModel> = mutableStateOf(
        DictionaryModel(
            data = listOf()
        )
    )

    val query: MutableState<String> = mutableStateOf("")

    val sharedSentence: MutableState<String> = mutableStateOf("")

    val searchLoading: MutableState<Boolean> = mutableStateOf(false)

    //val mightForgetItems: MutableState<List<WordReviewModelDto>> = mutableStateOf(listOf())

    val isHomeSelected: MutableState<Boolean> = mutableStateOf(true)

    val isReviewSelected: MutableState<Boolean> = mutableStateOf(false)

    // DICTIONARY STATE

    val currentKanji: MutableState<KanjiModel> = mutableStateOf(
        KanjiModel.Empty())

    val currentStory: MutableState<String> = mutableStateOf("")

    lateinit var kanjiDict: KanjiCollection

    lateinit var storiesDict: StoriesCollection

    init{
        viewModelScope.launch {
            kanjiDict = kanjiRepository.getKanjiDict(appContext)
            storiesDict = kanjiRepository.getKanjiStories(appContext)
        }
    }

    fun toggleHome(value: Boolean) {
        isHomeSelected.value = value
    }

    fun toggleReviews(value: Boolean) {
        isReviewSelected.value = value
    }

    fun setCurrentWord(word: DictionaryModel) {
        currentWordModel.value = word
    }

    fun setCurrentWordFromReview(word: WordReviewModelDto){
        viewModelScope.launch{
            searchLoading.value = true
            currentWordModel.value = reviewRepository.mapReviewToDomain(word)
            searchLoading.value = false
        }
    }

    fun setSharedText(text: String?) {
        query.value = text ?: ""
        textReceived.value = true
    }

    fun setSharedSentence(text: String?) {
        sentenceReceived.value = false
        sharedSentence.value = text ?: ""
        sentenceReceived.value = true
    }

    fun unsetSharedText() {
        textReceived.value = false
        query.value = ""
    }

    fun unsetSharedSentence() {
        sentenceReceived.value = false
    }

    fun setCurrentKanji(kanji: String)  {
        currentKanji.value = kanjiDict.kanjis?.get(kanji) ?: KanjiModel.Empty()
    }

    fun setCurrentStory(kanji: String)  {
        currentStory.value = storiesDict.stories?.get(kanji) ?: ""
    }

    fun onTriggerEvent(event: DictionaryEvent) {
        viewModelScope.launch {
            try {
                when(event) {
                    is DictionaryEvent.SearchWordEvent -> {
                        searchWord()
                    }
                }
            } catch(e: Exception) {
                Log.d(TAG, "Something wrong happened: ${e.cause}")
            }
        }
    }

    fun onQueryChanged(value: String) {
        query.value = value
    }

    /*
    fun getNReviews(n: Int){
        viewModelScope.launch {
            mightForgetItems.value = reviewRepository.getNReviewItems(n)
        }
    }*/

    fun addWordToReview() {
        viewModelScope.launch{
            reviewRepository.addToReview(currentWordModel.value)
        }
    }

    private suspend fun searchWord() {
        searchLoading.value = true
        val result = dictRepository.search(query.value.toLowerCase())
        currentWordModel.value = result
        searchLoading.value = false
    }
}