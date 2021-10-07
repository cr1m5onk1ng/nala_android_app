package com.example.nala.ui.ocr

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.repository.DictionaryRepository
import com.example.nala.services.ocr.ViewOCRService
import com.example.nala.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OCRViewModel @Inject constructor(
    private val ocrService: ViewOCRService,
    private val dictRepository: DictionaryRepository,
) : ViewModel() {

    val loadingDialogOpen = mutableStateOf(false)
    val sentenceDialogOpen = mutableStateOf(false)
    val recognizedText = mutableStateOf("")

    private val _ispectedSentence = MutableStateFlow<String>("")

    val inspectedSentence = _ispectedSentence.asStateFlow()

    @ExperimentalCoroutinesApi
    private val inspectedCaptionTokensFlow = _ispectedSentence.mapLatest { sentence ->
        dictRepository.tokenize(sentence)
    }

    val inspectedElementTokens: MutableState<List<String>> = mutableStateOf(listOf())

    val inspectedElementTokensMap : MutableState<Map<Pair<Int, Int>, String>> = mutableStateOf(mapOf())

    fun setView(view: View) {
        ocrService.setView(view)
    }

    fun setLoadingDialogOpen(value: Boolean) {
        loadingDialogOpen.value = value
    }

    fun setSentenceDialogOpen(value: Boolean) {
        sentenceDialogOpen.value = value
    }

    fun initModel() {
        viewModelScope.launch(Dispatchers.IO) {
            ocrService.initOcrModel()
        }
    }

    @ExperimentalCoroutinesApi
    fun getTextFromView() {
        viewModelScope.launch(Dispatchers.IO) {
            loadingDialogOpen.value = true
            sentenceDialogOpen.value = false
            val text = ocrService.recognize()
            withContext(Dispatchers.Main) {
                recognizedText.value = text
                val cleand = Utils.cleanRecognizedTextForJapanese(text)
                onInspectSentence(cleand)
                loadingDialogOpen.value = false
                sentenceDialogOpen.value = true
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun onInspectSentence(sentence: String) {
        viewModelScope.launch {
            _ispectedSentence.value = sentence
            inspectedCaptionTokensFlow.collect {
                inspectedElementTokens.value = it
                inspectedElementTokensMap.value = dictRepository.tokensToIndexMap(
                    tokens = it,
                    text = inspectedSentence.value
                )
            }
        }
    }

}