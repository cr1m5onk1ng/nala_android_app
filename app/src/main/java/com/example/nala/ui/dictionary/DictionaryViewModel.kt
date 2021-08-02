package com.example.nala.ui.dictionary

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.*
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.repository.DictionaryRepository
import com.example.nala.repository.KanjiRepository
import com.example.nala.repository.ReviewRepository
import com.example.nala.ui.DataState
import com.example.nala.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val dictRepository: DictionaryRepository,
    private val kanjiRepository: KanjiRepository,
    private val reviewRepository: ReviewRepository,
    @ApplicationContext appContext: Context,
) : ViewModel() {

    // SHARED TEXT AND SENTENCES STATE
    val sentenceReceived: MutableState<Boolean> = mutableStateOf(false)
    val textReceived: MutableState<Boolean> = mutableStateOf(false)
    val isWordFromIntent: MutableState<Boolean> = mutableStateOf(false)
    val isWordFromForm: MutableState<Boolean> = mutableStateOf(false)

    // HOME SCREEN STATE
    val sentenceState: MutableState<DataState<String>> = mutableStateOf(DataState.Initial(""))

    val mightForgetItemsState = MutableStateFlow<DataState<List<WordReviewModel>>>(DataState.Initial(listOf()))

    val addedToReview: MutableState<Boolean> = mutableStateOf(false)

    val query: MutableState<String> = mutableStateOf("")

    val wordSearchState: MutableState<DataState<DictionaryModel>> = mutableStateOf(DataState.Initial(
        DictionaryModel.Empty()))

    val kanjiSearchState: MutableState<DataState<KanjiModel>> = mutableStateOf(DataState.Initial(
        KanjiModel.Empty()))

    val kanjiStoryState: MutableState<DataState<String>> = mutableStateOf(DataState.Initial(""))

    val sharedSentenceTokens: MutableState<List<String>> = mutableStateOf(
        listOf()
    )

    val sharedSentenceTokensIndexMap : MutableState<Map<Pair<Int, Int>, String>> = mutableStateOf(mapOf())

    val editStoryFormActive: MutableState<Boolean> = mutableStateOf(false)

    // BOTTOM BAR STATE

    val isHomeSelected: MutableState<Boolean> = mutableStateOf(true)

    val isReviewSelected: MutableState<Boolean> = mutableStateOf(false)

    // DICTIONARY STATE

    val currentWordKanjis: MutableState<List<String>> = mutableStateOf(listOf())


    init{
        loadMightForgetItems()
    }


    private suspend fun searchWord() {
        wordSearchState.value = DataState.Loading
        val dictModel = dictRepository.search(query.value.toLowerCase())
        if (dictModel.isEmpty()) {
            wordSearchState.value = DataState.Error("Couldn't fetch word from API")
        } else {
            setCurrentWordKanjis(dictModel.word)
            wordSearchState.value = DataState.Success(dictModel)
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
        viewModelScope.launch {
            searchWord()
        }
    }

    fun setSharedSentence(text: String?) {
        viewModelScope.launch {
            sentenceReceived.value = true
            val actualText = text ?: ""
            if (actualText.isEmpty()) {
                sentenceState.value = DataState.Error("Invalid text")
            } else {
                sentenceState.value = DataState.Loading
                sharedSentenceTokens.value = dictRepository.tokenize(text?: "")
                sharedSentenceTokensIndexMap.value = dictRepository.tokensToIndexMap(
                    sharedSentenceTokens.value,
                    text ?: ""
                )
                sentenceState.value = DataState.Success(actualText)
            }
        }
    }

    fun unsetSharedText() {
        textReceived.value = false
        query.value = ""
    }

    fun unsetSharedSentence() {
        sentenceReceived.value = false
    }

    fun setIsWordFromForm(){
        isWordFromIntent.value = false
        isWordFromForm.value = true
    }

    fun setIsWordFromIntent(){
        isWordFromForm.value = false
        isWordFromIntent.value = true
    }

    fun setCurrentKanji(kanji: String)  {
        viewModelScope.launch {
            kanjiSearchState.value = DataState.Loading
            val kanji = kanjiRepository.getKanjiModel(kanji)
            if (kanji.isEmpty()){
                kanjiSearchState.value = DataState.Error("Couldn't find kanji: $kanji in dictionary")
            } else {
                kanjiSearchState.value = DataState.Success(kanji)
            }
        }
    }

    fun setCurrentWordKanjis(word: String) {
        viewModelScope.launch {
            val kanjiList = mutableListOf<String>()
            for(k in word) {
                //Log.d("KANJIDEBUG", "Searched character: $k")
                val kanji = kanjiRepository.getKanjiModel(k.toString())
                //Log.d("KANJIDEBUG", "Retrieved Kanji: $kanji")
                if (!kanji.isEmpty()){
                    kanjiList.add(kanji.kanji)
                }
            }
            //Log.d("KANJIDEBUG", "KANJI LIST: $kanjiList")
            currentWordKanjis.value = kanjiList
        }
    }

    fun setCurrentStory(kanji: String)  {
        viewModelScope.launch {
            kanjiStoryState.value = DataState.Loading
            val currentStory = kanjiRepository.getKanjiStory(kanji)
            if (currentStory.isEmpty()) {
                kanjiStoryState.value = DataState.Error("couldn't find story for this kanji")
            }
            kanjiStoryState.value = DataState.Success(currentStory)
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

    fun addWordToReview(wordModel: DictionaryModel) {
        viewModelScope.launch{
            addedToReview.value = false
            reviewRepository.addWordToReview(wordModel)
            addedToReview.value = true
            loadMightForgetItems()
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
            loadMightForgetItems()
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
            wordSearchState.value = DataState.Loading
            wordReviewModel?.let{
                val model = reviewRepository.getWordData(wordReviewModel)
                if (model.isEmpty()) {
                    wordSearchState.value = DataState.Error("could not fetch word data model from review")
                } else {
                    wordSearchState.value = DataState.Success(model)
                    setCurrentWordKanjis(model.word)
                }
            }
        }
    }

    fun setCurrentWordFromStudy(word: String) {
        viewModelScope.launch {
            wordSearchState.value = DataState.Loading
            val dbModel = reviewRepository.getWordReview(word)
            val model = if(dbModel != null) {
                reviewRepository.getWordData(dbModel)
            } else {
                dictRepository.search(word)
            }
            if(model.isEmpty()) {
                wordSearchState.value = DataState.Error("could not fetch word data")
            } else {
                wordSearchState.value = DataState.Success(model)
                setCurrentWordKanjis(model.word)
            }
        }
    }

    fun updateKanjiStory(kanji: String, story: String) {
        viewModelScope.launch {
            kanjiRepository.updateKanjiStory(kanji, story)
        }
    }

    fun loadMightForgetItems() {
        viewModelScope.launch {
            mightForgetItemsState.value = DataState.Loading
            reviewRepository.getWordReviews().collect{
                if(it.isEmpty()) {
                    mightForgetItemsState.value = DataState.Error("No item in list")
                } else {
                    mightForgetItemsState.value = DataState.Success(it)
                }
            }
        }
    }
}