package com.example.nala.services.ocr

import android.graphics.Bitmap

interface WindowScreenshotStrategy {
    fun takeScreenshot() : Bitmap?
}