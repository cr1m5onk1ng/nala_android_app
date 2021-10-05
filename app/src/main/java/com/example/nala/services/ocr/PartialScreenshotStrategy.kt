package com.example.nala.services.ocr

import android.graphics.Bitmap
import android.view.View

interface PartialScreenshotStrategy {
    fun takeScreenshot(view: View) : Bitmap?
}