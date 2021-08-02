package com.example.nala.ui.dictionary

import android.content.Context.WINDOW_SERVICE

import android.R
import android.content.Context

import android.graphics.PixelFormat

import android.os.Build
import android.util.Log
import android.view.*
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.example.nala.ui.composables.DictionaryWindowScreen
import java.lang.Exception
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory


class DictionaryWindow(
    private val context: Context,
    ) {
    // declaring required variables
    private var mParams: WindowManager.LayoutParams? = null
    private val overlayWindowManager by lazy {
        context.getSystemService(WINDOW_SERVICE)
                as WindowManager
    }
    private val windowDictionaryView = ComposeView(context)

    val factory = NewInstanceFactory()

    init {
        // set the layout parameters of the window
        val params = WindowManager.LayoutParams( // Shrink the window to wrap the content rather
            // than filling the screen
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,  // Display it on top of other application windows
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Don't let it grab the input focus
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  // Make the underlying application window visible
            // through any transparent parts
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.RIGHT or Gravity.TOP
        overlayWindowManager.addView(windowDictionaryView.apply {
            ViewTreeLifecycleOwner.set(this, findViewTreeLifecycleOwner())
            setContent {  Text("IT WORKS!") }
        }, params)
    }

    fun open() {
        try {
            // check if the view is already
            // inflated or present in the window
            if (windowDictionaryView.windowToken == null) {
                if (windowDictionaryView.parent == null) {
                    overlayWindowManager.addView(windowDictionaryView, mParams)
                }
            }
        } catch (e: Exception) {
            Log.d("DICTWINDOWDEBUG", "Error while opening window: $e")
        }
    }

    fun close() {
        try {
            // remove the view from the window
            (context.getSystemService(WINDOW_SERVICE) as WindowManager).removeView(windowDictionaryView)
            // invalidate the view
            windowDictionaryView.invalidate()
            // remove all views
            (windowDictionaryView.parent as ViewGroup).removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("DICTWINDOWDEBUG", "Error while closing window: $e")
        }
    }
}