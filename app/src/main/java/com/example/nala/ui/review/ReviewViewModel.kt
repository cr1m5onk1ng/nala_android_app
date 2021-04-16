package com.example.nala.ui.review

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.db.models.review.WordReviewModelDto
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    //TODO(Find a better way)
    init {
        viewModelScope.launch{
            initialReviewItems = reviewRepository.getNReviewItems(10)
        }
    }

    lateinit var initialReviewItems: List<WordReviewModelDto>

    val reviewsLoading: MutableState<Boolean> = mutableStateOf(false)
    val reviewItems: MutableState<List<WordReviewModelDto>> = mutableStateOf(listOf())

    fun updateReviewItem(quality: Int, reviewModel: WordReviewModelDto) {
        viewModelScope.launch {
            reviewRepository.updateReviewParameters(quality, reviewModel)
            loadReviewItems()
        }
    }

    fun loadReviewItems() {
        viewModelScope.launch {
            reviewsLoading.value = true
            reviewItems.value = reviewRepository.getNReviewItems(30)
            reviewsLoading.value = false
        }
    }


}