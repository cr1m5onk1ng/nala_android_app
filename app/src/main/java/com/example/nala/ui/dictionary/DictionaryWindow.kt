package com.example.nala.ui.dictionary

import android.content.Context.WINDOW_SERVICE
import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.*
import java.lang.Exception
import android.view.LayoutInflater
import android.widget.Button
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import com.example.nala.R
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.repository.DictionaryRepository
import com.example.nala.ui.adapters.SenseItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class DictionaryWindow (
    private val context: Context,
) {

    val metrics: DisplayMetrics = context.applicationContext.resources.displayMetrics
    val width = metrics.widthPixels
    val height = metrics.heightPixels
    val popupWidth = width * (0.64f).toInt()
    val popupHeight = height + (0.88f).toInt()
    private var mParams: WindowManager.LayoutParams = WindowManager.LayoutParams( // Shrink the window to wrap the content rather
        // than filling the screen
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
         else WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        // through any transparent parts
        PixelFormat.TRANSLUCENT
    )
    private var overlayWindowManager: WindowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager

    private var layoutInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var windowDictionaryView: View = layoutInflater.inflate(R.layout.dictionary_popup, null)
    private var loadingScreenView: View = layoutInflater.inflate(R.layout.data_loading_screen, null)


    init {
        // set the layout parameters of the window
        windowDictionaryView.findViewById<ImageView>(R.id.window_close).setOnClickListener{
            close()
        }
        windowDictionaryView.setOnTouchListener{ view, event ->
            val updatedParams = mParams
            var initialX: Int = 0
            var initialY: Int = 0
            var initialTouchX: Float = 0f
            var initialTouchY: Float = 0f
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = updatedParams.x
                    initialY = updatedParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    view.performClick()
                }
                MotionEvent.ACTION_UP -> {

                }
                MotionEvent.ACTION_MOVE -> {
                    updatedParams.x = initialX + (event.rawX - initialTouchX).toInt()
                    updatedParams.y = initialY + (event.rawY - initialTouchY).toInt()
                    overlayWindowManager.updateViewLayout(windowDictionaryView, updatedParams)
                    view.performClick()
                }
            }
            view.performClick()
        }
        mParams.gravity = Gravity.CENTER
        mParams.x = 0
        mParams.y = 0
    }

    fun addView(view: View, params: WindowManager.LayoutParams) {
        try {
            // check if the view is already
            // inflated or present in the window
            if (windowDictionaryView.windowToken == null) {
                if (windowDictionaryView.parent == null) {
                    overlayWindowManager.addView(view, params)
                }
            }
        } catch (e: Exception) {
            Log.d("DICTWINDOWDEBUG", "Error while opening window: $e")
        }
    }

    fun open(word: DictionaryModel) {
        removeView(loadingScreenView)
        bindData(word)
        addView(windowDictionaryView, mParams)
    }

    fun setLoadingScreen() {
        addView(loadingScreenView, mParams)
    }

    fun removeView(view: View) {
        try {
            // remove the view from the window
            (context.getSystemService(WINDOW_SERVICE) as WindowManager).removeView(view)
            // invalidate the view
            view.invalidate()
            // remove all views
            (view.parent as ViewGroup).removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("DICTWINDOWDEBUG", "Error while closing window: $e")
        }
    }

    fun setListeners() {
        // Here go the button listeners
        //  this function will be called in the service onCreate
        // to set up all the needed listeners

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

    private fun bindData(word: DictionaryModel) {
        if (!word.isEmpty()) {
            val definitions: List<String> = word?.senses?.map {
                val definitions = it?.englishDefinitions ?: listOf()
                val defString = definitions.joinToString(separator=", ")
                defString
            }
            val listAdapter = SenseItemAdapter().apply {
                submitList(definitions)
            }
            val sensesView =  windowDictionaryView.findViewById<RecyclerView>(R.id.rvSensesList)
            sensesView.apply{
                layoutManager = LinearLayoutManager(context)
                adapter = listAdapter
            }
            windowDictionaryView.findViewById<TextView>(R.id.tvWord).text = word.word
            windowDictionaryView.findViewById<TextView>(R.id.tvFurigana).text = word.reading
        }
    }

    private fun updateView(view: View, params: WindowManager.LayoutParams) {
        overlayWindowManager.updateViewLayout(view, params)
    }
}