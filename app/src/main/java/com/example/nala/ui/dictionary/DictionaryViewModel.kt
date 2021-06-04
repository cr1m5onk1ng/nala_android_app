package com.example.nala.ui.dictionary

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.*
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.kanji.KanjiCollection
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.kanji.StoriesCollection
import com.example.nala.domain.model.review.SentenceReviewModel
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

    val sentenceLoading: MutableState<Boolean> = mutableStateOf(true)

    val mightForgetItemsLoaded: MutableState<Boolean> = mutableStateOf(false)

    val addedToReview: MutableState<Boolean> = mutableStateOf(false)

    val currentWordModel: MutableState<DictionaryModel> = mutableStateOf(
        DictionaryModel.Empty()
    )

    val currentSentence: MutableState<String> = mutableStateOf("")

    val query: MutableState<String> = mutableStateOf("")

    val sharedSentence: MutableState<String> = mutableStateOf("")

    val sharedSentenceTokens: MutableState<List<String>> = mutableStateOf(
        listOf()
    )

    val sharedSentenceTokensIndexMap : MutableState<Map<Pair<Int, Int>, String>> = mutableStateOf(mapOf())

    val searchLoading: MutableState<Boolean> = mutableStateOf(false)

    val kanjiSet: MutableState<Boolean> = mutableStateOf(false)

    val storySet: MutableState<Boolean> = mutableStateOf(false)

    val editStoryFormActive: MutableState<Boolean> = mutableStateOf(false)

    // BOTTOM BAR STATE

    val isHomeSelected: MutableState<Boolean> = mutableStateOf(true)

    val isReviewSelected: MutableState<Boolean> = mutableStateOf(false)

    // DICTIONARY STATE

    val currentKanji: MutableState<KanjiModel> = mutableStateOf(
        KanjiModel.Empty())

    val currentWordKanjis: MutableState<List<String>> = mutableStateOf(listOf())

    val currentStory: MutableState<String> = mutableStateOf("")

    val mightForgetItems: MutableState<List<WordReviewModel>> = mutableStateOf(listOf())

    //lateinit var kanjiDict: KanjiCollection
    //lateinit var storyDict: StoriesCollection


    init{

        /*
        viewModelScope.launch {
            kanjiDict = kanjiRepository.getKanjiDict(appContext)
            storyDict = kanjiRepository.getKanjiStories(appContext)
            kanjiRepository.populateKanjiDatabase(kanjiDict, storyDict)
        } */

        viewModelScope.launch {
            mightForgetItemsLoaded.value = false
            val lastItems = reviewRepository.getWordReviews().takeLast(10)
            mightForgetItems.value = lastItems
            mightForgetItemsLoaded.value = true
        }
    }


    fun toggleHome(value: Boolean) {
        isHomeSelected.value = value
    }

    fun toggleReviews(value: Boolean) {
        isReviewSelected.value = value
    }

    fun toggleEditStoryForm(value: Boolean) {
        editStoryFormActive.value = value
    }

    fun setSharedText(text: String?) {
        textReceived.value = true
        query.value = text ?: ""
    }

    fun setSharedSentence(text: String?) {
        viewModelScope.launch {
            sentenceReceived.value = true
            sentenceLoading.value = true
            sharedSentenceTokens.value = dictRepository.tokenize(text?: "")
            sharedSentenceTokensIndexMap.value = dictRepository.tokensToIndexMap(
                sharedSentenceTokens.value,
                text ?: ""
            )
            sharedSentence.value = text ?: ""
            sentenceLoading.value = false
        }
    }

    fun unsetSharedText() {
        textReceived.value = false
        query.value = ""
    }

    fun unsetSharedSentence() {
        sentenceReceived.value = false
    }

    fun setCurrentKanji(kanji: String)  {
        viewModelScope.launch {
            kanjiSet.value = false
            currentKanji.value = kanjiRepository.getKanjiModel(kanji)
            kanjiSet.value = true
        }
    }

    fun setCurrentWordKanjis(word: String) {
        viewModelScope.launch {
            val kanjiList = mutableListOf<String>()
            for(k in word) {
                Log.d("KANJIDEBUG", "Searched character: $k")
                val kanji = kanjiRepository.getKanjiModel(k.toString())
                Log.d("KANJIDEBUG", "Retrieved Kanji: $kanji")
                if (!kanji.isEmpty()){
                    kanjiList.add(kanji.kanji)
                }
            }
            Log.d("KANJIDEBUG", "KANJI LIST: $kanjiList")
            currentWordKanjis.value = kanjiList
        }
    }

    fun setCurrentStory(kanji: String)  {
        viewModelScope.launch {
            storySet.value = false
            currentStory.value = kanjiRepository.getKanjiStory(kanji)
            storySet.value = true
        }
    }

    fun onTriggerEvent(event: DictionaryEvent) {
        viewModelScope.launch {
            try {
                when(event) {
                    is DictionaryEvent.SearchWordEvent -> {
                        searchWord()
                    }
                    else -> {}
                }
            } catch(e: Exception) {
                Log.d(TAG, "Something wrong happened: ${e.cause}")
            }
        }
    }

    fun onQueryChanged(value: String) {
        query.value = value
    }

    fun addWordToReview() {
        viewModelScope.launch{
            addedToReview.value = false
            reviewRepository.addWordToReview(currentWordModel.value)
            addedToReview.value = true
        }
    }

    fun addSentenceToReview(word: String, sentence: String) {
        viewModelScope.launch{
            addedToReview.value = false
            val reviewModel = SentenceReviewModel(
                sentence = sentence,
                targetWord = word,
            )
            val cachedWord = reviewRepository.getWordReview(word)
            if(cachedWord == null) {
                val word = dictRepository.search(query.value.toLowerCase())
                if(!word.isEmpty()) {
                    reviewRepository.addWordToReview(word)
                }
            }
            reviewRepository.addSentenceToReview(reviewModel)
            addedToReview.value = true
        }
    }

    fun addKanjiToReview(kanjiModel: KanjiModel) {
        viewModelScope.launch{
            addedToReview.value = false
            val meanings = kanjiModel?.meaning ?: listOf()
            val kunReadings = kanjiModel?.kunReadings ?: listOf()
            val onReadings = kanjiModel?.onReadings ?: listOf()
            reviewRepository.addKanjiToReview(kanjiModel ?: KanjiModel.Empty())
            reviewRepository.addKanjiMeaningsToReview(meanings, kanjiModel.kanji)
            reviewRepository.addKanjiKunReadingsToReview(kunReadings, kanjiModel.kanji)
            reviewRepository.addKanjiOnReadingsToReview(onReadings, kanjiModel.kanji)
            addedToReview.value = true
        }
    }

    fun setCurrentWordFromReview(wordReviewModel: WordReviewModel?) {
        viewModelScope.launch {
            searchLoading.value = true
            wordReviewModel?.let{
                val model = reviewRepository.getWordData(wordReviewModel)
                currentWordModel.value = model
                currentSentence.value = ""
                searchLoading.value = false
            }
        }
    }

    fun updateKanjiStory(kanji: String, story: String) {
        viewModelScope.launch {
            kanjiRepository.updateKanjiStory(kanji, story)
        }
    }

    fun setCurrentSentenceFromReview(word: String, sentence: String){
        viewModelScope.launch {
            searchLoading.value = true
            val reviewModel = reviewRepository.getWordReview(word)
            setCurrentWordFromReview(reviewModel)
            currentSentence.value = sentence
            searchLoading.value = false
        }
    }

    private suspend fun searchWord() {
        searchLoading.value = true
        val dictModel = dictRepository.search(query.value.toLowerCase())
        currentWordModel.value = dictModel
        setCurrentWordKanjis(dictModel.word)
        searchLoading.value = false
    }
}