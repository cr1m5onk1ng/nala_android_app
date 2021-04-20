package com.example.nala.ui.study

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.repository.DictionaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    val currentStudyContext: MutableState<String> = mutableStateOf("")
    val currentStudyTargetWord: MutableState<DictionaryModel> = mutableStateOf(
        DictionaryModel.Empty()
    )

    val selectedWord: MutableState<String> = mutableStateOf("")

    val contextLoading: MutableState<Boolean> = mutableStateOf(false)

    val wordModelLoading: MutableState<Boolean> = mutableStateOf(false)

    fun setSelectedWord(text: String){
        selectedWord.value = text
    }

    fun setStudyContext(sentence: String?) {
        contextLoading.value = true
        currentStudyContext.value = sentence ?: ""
        contextLoading.value = false
    }

    fun setStudyTargetWord(word: String) {
        wordModelLoading.value = true
        viewModelScope.launch {
            val wordModel = dictionaryRepository.search(word)
            currentStudyTargetWord.value = wordModel
            wordModelLoading.value = false
        }
    }

}