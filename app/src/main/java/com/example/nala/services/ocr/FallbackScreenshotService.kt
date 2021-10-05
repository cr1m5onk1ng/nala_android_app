package com.example.nala.services.ocr

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import eu.bolt.screenshotty.*
import eu.bolt.screenshotty.coroutines.makeScreenshotAsync
import eu.bolt.screenshotty.util.ScreenshotFileSaver
import java.io.File

class FallbackScreenshotService(
    private val activity: Activity,
) : AsyncOCRService{

    companion object {
        private const val REQUEST_SCREENSHOT_PERMISSION = 888
    }

    private var screenshotManager: ScreenshotManager? = null
    private var currentSubscription: ScreenshotResult.Subscription? = null
    private var tesseractOCR: TesseractOCR? = null
    private var result = ""

    init {
        screenshotManager = ScreenshotManagerBuilder(activity)
            .withCustomActionOrder(ScreenshotActionOrder.pixelCopyFirst()) //optional, ScreenshotActionOrder.pixelCopyFirst() by default
            .withPermissionRequestCode(REQUEST_SCREENSHOT_PERMISSION) //optional, 888 by default
            .build()
        tesseractOCR = TesseractOCR(activity, "jpn")
        tesseractOCR!!.init()
    }

    override fun recognize()  {
        currentSubscription?.dispose()
        val screenshotResult = screenshotManager?.makeScreenshot()
        currentSubscription = screenshotResult?.observe(
            onSuccess = {
                processScreenshot(it)
                Log.d("OCR_DEBUG", "Recognized Text: ${this.result}")
            },
            onError = { throw it }
        )
    }

    fun getResult() : String {
        return this.result
    }

    fun saveScreenshot(screenshot: Screenshot) : File {
        val fileSaver = ScreenshotFileSaver.create(Bitmap.CompressFormat.PNG)
        val targetFile = File(activity.applicationContext.filesDir, "screenshot")
        fileSaver.saveToFile(targetFile, screenshot)
        return targetFile
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        screenshotManager?.onActivityResult(requestCode, resultCode, data)
    }

    fun dispose() {
        currentSubscription?.dispose()
        tesseractOCR?.close()
    }

    private fun processScreenshot(screenshot: Screenshot) {
        assert(tesseractOCR!!.isInitialized())
        val bitmap = when (screenshot) {
            is ScreenshotBitmap -> screenshot.bitmap
        }
        this.result = tesseractOCR!!.recognize(bitmap)
    }

}