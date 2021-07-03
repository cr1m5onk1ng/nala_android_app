package com.example.nala.ui.review

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.KanjiReviewModel
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.review.ReviewCategory
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    val reviewsLoading: MutableState<Boolean> = mutableStateOf(false)
    val addedToReview: MutableState<Boolean> = mutableStateOf(false)
    val currentWordSet: MutableState<Boolean> = mutableStateOf(false)
    val wordReviewItems: MutableState<List<WordReviewModel>> = mutableStateOf(listOf())
    val sentenceReviewItems: MutableState<List<SentenceReviewModel>> = mutableStateOf(listOf())
    val kanjiReviewItems: MutableState<List<KanjiReviewModel>> = mutableStateOf(listOf())

    init {
        loadWordReviewItems()
    }

    val selectedCategory: MutableState<ReviewCategory> = mutableStateOf(ReviewCategory.Word)

    fun updateWordReviewItem(quality: Int, reviewModel: WordReviewModel) {
        viewModelScope.launch {
            reviewRepository.updateWordReviewParameters(quality, reviewModel)
            //loadWordReviewItems()
        }
    }

    fun updateSentenceReviewItem(quality: Int, sentenceReviewModel: SentenceReviewModel) {
        viewModelScope.launch {
            reviewRepository.updateSentenceReviewParameters(quality, sentenceReviewModel)
            //loadSentenceReviewItems()
        }
    }

    fun updateKanjiReviewItem(quality: Int, kanjiReviewModel: KanjiReviewModel) {
        viewModelScope.launch {
            reviewRepository.updateKanjiReviewParameters(quality, kanjiReviewModel)
            //loadWordReviewItems()
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

    fun dismissWordReviewItem(word: String) {
        viewModelScope.launch{
            reviewsLoading.value = true
            wordReviewItems.value = wordReviewItems.value.filter {
                it.word != word
            }
            reviewsLoading.value = false
        }
    }

    fun dismissSentenceReviewItem(sentence: String){
        viewModelScope.launch{
            reviewsLoading.value = true
            sentenceReviewItems.value = sentenceReviewItems.value.filter {
                it.sentence != sentence
            }
            reviewsLoading.value = false
        }
    }

    fun dismissKanjiReviewItem(kanji: String){

        viewModelScope.launch{
            reviewsLoading.value = true
            kanjiReviewItems.value = kanjiReviewItems.value.filter {
                it.kanji != kanji
            }
            reviewsLoading.value = false
        }
    }

    fun loadWordReviewItems() {
        viewModelScope.launch {
            reviewsLoading.value = true
            wordReviewItems.value = reviewRepository.getNWordReviews(30)
            reviewsLoading.value = false
        }
    }

    fun loadSentenceReviewItems() {

        viewModelScope.launch {
            reviewsLoading.value = true
            sentenceReviewItems.value = reviewRepository.getNSentenceReviewItems(30)
            reviewsLoading.value = false
        }

    }

    fun loadKanjiReviewItems() {
        viewModelScope.launch {
            reviewsLoading.value = true
            kanjiReviewItems.value = reviewRepository.getNKanjiReviewItems(30)
            reviewsLoading.value = false
        }

    }

    fun setCategory (category: ReviewCategory) {
        selectedCategory.value = category
    }

    fun addArticleToChronology(url: String) {
        viewModelScope.launch{
            reviewRepository.addArticleToFavorites(url)
        }
    }
}