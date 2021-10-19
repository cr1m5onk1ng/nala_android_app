package com.example.nala.services.ocr

import android.content.Context
import android.util.Log
import android.view.View
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ViewOCRService @Inject constructor(
    @ApplicationContext context: Context
) : WindowOCRService {
    private var tesseractOcr: TesseractOCR? = null
    private var screenshotStrategy: ViewScreenshotStrategy? = null
    private var currentView: View? = null

    init {
        tesseractOcr = TesseractOCR(context, "jpn")
        screenshotStrategy = ViewScreenshotStrategy()
    }

    override fun recognize(): String {
        assert(currentView != null)
        if(!tesseractOcr!!.isInitialized()) {
            tesseractOcr!!.init()
        }
        val screenshot = screenshotStrategy!!.takeScreenshot(currentView!!)
        if (screenshot == null) {
            Log.d("OCR_DEBUG", "Couldn't take screenshot")
            return ""
        }
        val recognizedText = tesseractOcr!!.recognize(screenshot!!)
        Log.d("OCR_DEBUG", "RECOGNIZED: $recognizedText")
        return recognizedText
    }

    fun setView(view: View) {
        this.currentView = view
    }

    fun initOcrModel() {
        tesseractOcr!!.init()
    }

    fun dispose() {
        tesseractOcr?.close()
    }

}