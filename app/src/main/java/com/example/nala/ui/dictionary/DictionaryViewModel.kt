package com.example.nala.ui.dictionary

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.*
import com.example.nala.db.models.review.relations.WordReviewWithSenses
import com.example.nala.db.models.review.relations.WordSenseWithDefinitions
import com.example.nala.db.models.review.relations.WordSenseWithTags
import com.example.nala.db.models.review.relations.WordWithTags
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.dictionary.Sense
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

    val mightForgetItemsLoaded: MutableState<Boolean> = mutableStateOf(false)

    val addedToReview: MutableState<Boolean> = mutableStateOf(false)

    val currentWordModel: MutableState<DictionaryModel> = mutableStateOf(
        DictionaryModel.Empty()
    )

    val currentSentence: MutableState<String> = mutableStateOf("")


    val query: MutableState<String> = mutableStateOf("")

    val sharedSentence: MutableState<String> = mutableStateOf("")

    val sharedSentenceTokens : MutableState<List<String>> = mutableStateOf(listOf())

    val searchLoading: MutableState<Boolean> = mutableStateOf(false)



    val isHomeSelected: MutableState<Boolean> = mutableStateOf(true)

    val isReviewSelected: MutableState<Boolean> = mutableStateOf(false)

    // DICTIONARY STATE

    val currentKanji: MutableState<KanjiModel> = mutableStateOf(
        KanjiModel.Empty())

    val currentStory: MutableState<String> = mutableStateOf("")

   val mightForgetItems: MutableState<List<WordReviewModel>> = mutableStateOf(listOf())

    lateinit var kanjiDict: KanjiCollection

    lateinit var storiesDict: StoriesCollection


    init{

        viewModelScope.launch {
            kanjiDict = kanjiRepository.getKanjiDict(appContext)
            storiesDict = kanjiRepository.getKanjiStories(appContext)
        }

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

    fun setSharedText(text: String?) {
        query.value = text ?: ""
        textReceived.value = true
    }

    fun setSharedSentence(text: String?) {
        sentenceReceived.value = false
        viewModelScope.launch {
            sharedSentenceTokens.value = dictRepository.tokenize(text?: "")
            sharedSentence.value = text ?: ""
        }
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
        val kanjiModel = kanjiDict.kanjis[kanji] ?: KanjiModel.Empty()
        currentKanji.value = kanjiModel
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
        addedToReview.value = false
        viewModelScope.launch{
            reviewRepository.addWordToReview(currentWordModel.value)
            reviewRepository.addSensesToReview(
                senses=currentWordModel.value.senses,
                word=currentWordModel.value.word)
            reviewRepository.addWordTagsToReview(
                currentWordModel.value.dataTags, currentWordModel.value.word)
        }
        addedToReview.value = true
    }

    fun addSentenceToReview(word: String, sentence: String) {
        addedToReview.value = false
        viewModelScope.launch{
            val reviewModel = SentenceReviewModel(
                sentence = sentence,
                targetWord = word,
            )
            reviewRepository.addSentenceToReview(reviewModel)
        }
        addedToReview.value = true
    }

    fun addKanjiToReview(kanji: String) {
        addedToReview.value = false
        viewModelScope.launch{
            val kanjiModel = kanjiDict.kanjis[kanji]
            val kanjiReview = KanjiReviewModel(
                kanji = kanjiModel?.kanji ?: "",
                freq = kanjiModel?.freq ?: "",
                grade = kanjiModel?.grade ?: "",
                jlpt = kanjiModel?.jlpt ?: "",
                strokes = null,
            )
            val meanings = kanjiModel?.meaning ?: listOf()
            val kunReadings = kanjiModel?.kunReadings ?: listOf()
            val onReadings = kanjiModel?.onReadings ?: listOf()
            reviewRepository.addKanjiMeaningsToReview(meanings, kanji)
            reviewRepository.addKanjiKunReadingsToReview(kunReadings, kanji)
            reviewRepository.addKanjiOnReadingsToReview(onReadings, kanji)
        }
        addedToReview.value = true
    }

    fun setCurrentWordFromReview(wordReviewModel: WordReviewModel) {
        searchLoading.value = true
        viewModelScope.launch {
            val model = reviewRepository.getWordData(wordReviewModel)
            currentWordModel.value = model
            currentSentence.value = ""
        }
        searchLoading.value = false
    }

    fun setCurrentSentenceFromReview(word: String, sentence: String){
        searchLoading.value = true
        viewModelScope.launch {
            val reviewModel = reviewRepository.getWordReview(word)
            setCurrentWordFromReview(reviewModel)
            currentSentence.value = sentence
        }
        searchLoading.value = false
    }

    private suspend fun searchWord() {
        searchLoading.value = true
        val dictModel = dictRepository.search(query.value.toLowerCase())
        currentWordModel.value = dictModel
        searchLoading.value = false
    }
}