package com.example.nala.ui.ocr

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.nala.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import java.lang.Exception

class OCRWindow(
    private val context: Context,
    private val onRecognize: () -> Unit,
) {
    val metrics: DisplayMetrics = context.applicationContext.resources.displayMetrics
    val width = metrics.widthPixels
    val height = metrics.heightPixels
    val ocrButtonWidth = 24
    val ocrButtonHeight = 24
    private var ocrButtonParams: WindowManager.LayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    private var recTextParams : WindowManager.LayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    private var overlayWindowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var layoutInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var ocrButtonView: View = layoutInflater.inflate(R.layout.ocr_fab_button, null)
    private var recognizedTexgtView: View = layoutInflater.inflate(R.layout.recognized_text, null)
    private var loadingScreenView: View = layoutInflater.inflate(R.layout.data_loading_screen, null)

    init {
        setListeners()
    }

    fun setRecognizedText(text: String) {
        recognizedTexgtView.findViewById<TextView>(R.id.tvRecognizedText).text = text
        removeView(loadingScreenView)
        addView(recognizedTexgtView, ocrButtonParams)
    }

    fun setLoadingScreen() {
        removeView(ocrButtonView)
        addView(loadingScreenView, recTextParams)
    }

    fun initWindow() {
        addView(ocrButtonView, ocrButtonParams)
    }

    private fun addView(view: View, params: WindowManager.LayoutParams) {
        try {
            // check if the view is already
            // inflated or present in the window
            /*
        if (windowDictionaryView.windowToken == null) {
            if (windowDictionaryView.parent == null) {
                overlayWindowManager.addView(view, params)
            }
        } */
            overlayWindowManager.addView(view, params)
        } catch (e: Exception) {
            Log.d("OCRWINDOW", "Error while opening window: $e")
        }
    }

    private fun setListeners() {
        ocrButtonView.findViewById<Button>(R.id.btnRecgonize).setOnClickListener{
            onRecognize()
        }
        // TODO Add draggable listener
        ocrButtonParams.gravity = Gravity.CENTER
        ocrButtonParams.x = 0
        ocrButtonParams.y = 0

        recTextParams.gravity = Gravity.BOTTOM
        recTextParams.x = 0
        recTextParams.y = 0
    }

    fun close() {
        try {
            // remove the view from the window
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(ocrButtonView)
            // invalidate the view
            ocrButtonView.invalidate()
            recognizedTexgtView.invalidate()
            // remove all views
            (ocrButtonView.parent as ViewGroup).removeAllViews()
            (recognizedTexgtView.parent as ViewGroup).removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("DICTWINDOWDEBUG", "Error while closing window: $e")
        }
    }

    private fun removeView(view: View) {
        try {
            // remove the view from the window
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(view)
            // invalidate the view
            view.invalidate()
            // remove all views
            (view.parent as ViewGroup).removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("OCRWINDOW", "Error while closing window: $e")
        }
    }

}