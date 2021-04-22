package com.example.nala.ui.study

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.repository.DictionaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    val currentStudyContext: MutableState<String> = mutableStateOf("")

    val currentStudyTargetWord: MutableState<DictionaryModel> = mutableStateOf(
        DictionaryModel.Empty()
    )
    //TODO(Substitute simple strings with appropriate model)
    val similarSentences: MutableState<List<String>> = mutableStateOf(listOf())

    val selectedWord: MutableState<String> = mutableStateOf("")

    val contextLoading: MutableState<Boolean> = mutableStateOf(true)

    val wordModelLoading: MutableState<Boolean> = mutableStateOf(true)

    val similarSentencesLoading: MutableState<Boolean> = mutableStateOf(false)

    //TODO
    fun loadSimilarSentences() {

        viewModelScope.launch {
            similarSentencesLoading.value = true
            delay(2000)
            similarSentences.value = listOf(
                "殺人 略奪 治安維持も無く力は力でしか抗えない犯罪の5割はアンドロイド",
                "僕らが信じる真実は誰かの創作かもしれない僕らが見てるこの世界は",
                "風がそよぎ 海が凪ぎ空に虫と鳥が戯れる木々は今青々と",
                "説教じみた話じゃつまらない分かってるだからこそ感じて経験は何よりも饒舌そしてそれを忘れちゃいけないよ",
                "どう? 理解できたかなこれが人類の原風景上映はこれにて終了です",
            )
            similarSentencesLoading.value = false
        }
    }

    fun setSelectedWord(text: String){
        selectedWord.value = text
    }

    fun setStudyContext(sentence: String?) {
        contextLoading.value = true
        currentStudyContext.value = sentence ?: ""
        similarSentences.value = listOf()
        contextLoading.value = false
    }

    fun setStudyTargetWord(word: String) {
        viewModelScope.launch {
            wordModelLoading.value = true
            val wordModel = dictionaryRepository.search(word)
            currentStudyTargetWord.value = wordModel
            wordModelLoading.value = false
        }
    }

}