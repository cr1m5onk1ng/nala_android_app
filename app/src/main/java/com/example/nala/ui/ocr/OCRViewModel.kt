package com.example.nala.ui.ocr

import android.view.View
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.services.ocr.ViewOCRService
import com.example.nala.services.ocr.WindowOCRService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OCRViewModel @Inject constructor(
    private val ocrService: ViewOCRService
) : ViewModel() {

    val ocrLoading = mutableStateOf(false)
    val recognizedText = mutableStateOf("")

    fun setOcrLoading(value: Boolean) {
        ocrLoading.value = value
    }

    fun setView(view: View) {
        ocrService.setView(view)
    }

    fun getTextFromView() {
        viewModelScope.launch {
            ocrLoading.value = true
            val text = ocrService.recognize()
            recognizedText.value = text
            ocrLoading.value = false
        }
    }

}