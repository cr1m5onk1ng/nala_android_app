package com.example.nala.ui.dictionary

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.kanji.KanjiCollection
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.kanji.StoriesCollection
import com.example.nala.repository.DictionaryRepository
import com.example.nala.repository.KanjiRepository
import com.example.nala.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val dictRepository: DictionaryRepository,
    private val kanjiRepository: KanjiRepository,
    @ApplicationContext appContext: Context,
) : ViewModel() {

    val textReceived: MutableState<Boolean> = mutableStateOf(false)

    val currentWordModel: MutableState<DictionaryModel> = mutableStateOf(
        DictionaryModel(
            data = listOf()
        )
    )

    val query: MutableState<String> = mutableStateOf("")

    val searchLoading: MutableState<Boolean> = mutableStateOf(false)

    val reviewsLoading: MutableState<Boolean> = mutableStateOf(false)

    val currentKanji: MutableState<KanjiModel> = mutableStateOf(
        KanjiModel.Empty())

    val currentStory: MutableState<String> = mutableStateOf("")

    val reviewItems: MutableState<List<DictionaryModel>> = mutableStateOf(listOf())

    val mightForgetItems: MutableState<List<DictionaryModel>> = mutableStateOf(listOf())

    val isHomeSelected: MutableState<Boolean> = mutableStateOf(true)

    val isReviewSelected: MutableState<Boolean> = mutableStateOf(false)

    lateinit var kanjiDict: KanjiCollection

    lateinit var storiesDict: StoriesCollection

    init{
        viewModelScope.launch {
            kanjiDict = kanjiRepository.getKanjiDict(appContext)
            storiesDict = kanjiRepository.getKanjiStories(appContext)
        }
        getNReviews(20)
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

    fun setSharedText(text: String?) {
        query.value = text ?: ""
        textReceived.value = true
    }

    fun unsetSharedText() {
        textReceived.value = false
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

                    is DictionaryEvent.LoadReviewsEvent -> {
                        loadReviewItems()
                    }

                    is DictionaryEvent.AddReviewEvent -> {
                        addWordToReview()
                    }
                }
            } catch(e: Exception) {
                Log.d(TAG, "Something wrong happened: ${e.cause}")
            }
        }
    }

    private suspend fun searchWord() {
        searchLoading.value = true
        val result = dictRepository.search(query.value)
        currentWordModel.value = result
        searchLoading.value = false
    }

    fun onQueryChanged(value: String) {
        query.value = value
    }

    fun getNReviews(n: Int){
        viewModelScope.launch {
            mightForgetItems.value = dictRepository.getNReviewItems(n)
        }
    }

    private fun addWordToReview() {
        viewModelScope.launch{
            dictRepository.addToReview(currentWordModel.value)
        }
    }

    private fun loadReviewItems() {
        viewModelScope.launch {
            reviewsLoading.value = true
            reviewItems.value = dictRepository.getReviewItems()
            reviewsLoading.value = false
        }
    }

}