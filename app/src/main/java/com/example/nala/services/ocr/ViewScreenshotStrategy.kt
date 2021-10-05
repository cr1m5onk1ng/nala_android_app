package com.example.nala.services.ocr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View

class ViewScreenshotStrategy(
) : PartialScreenshotStrategy {

    override fun takeScreenshot(view: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            view.draw(canvas)
        } catch (e: Exception) {
            Log.d("OCR_DEBUG", "Failed to capture screenshot because:" + e.message)
        }
        // return the bitmap
        return screenshot
    }

}