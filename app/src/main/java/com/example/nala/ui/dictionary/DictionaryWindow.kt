package com.example.nala.ui.dictionary

import android.annotation.SuppressLint
import android.content.Context.WINDOW_SERVICE
import android.content.Context
import android.graphics.Paint
import android.graphics.PixelFormat
import android.util.Log
import android.view.*
import java.lang.Exception
import android.view.LayoutInflater
import com.example.nala.R
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nala.db.models.kanji.KanjiStories
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.repository.ReviewRepository
import com.example.nala.ui.adapters.dictionary.KanjiListAdapter
import com.example.nala.ui.adapters.dictionary.SenseItemAdapter
import com.example.nala.ui.adapters.dictionary.WordTagAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class DictionaryWindow (
    private val context: Context,
    private val reviewRepo: ReviewRepository,
    private val scope: CoroutineScope,
) {

    private lateinit var currentWord: DictionaryModel
    private lateinit var wordKanjis: List<KanjiModel>
    private lateinit var kanjiStories: List<KanjiStories>
    private lateinit var wordReviews: MutableList<String>
    private lateinit var kanjiReviews: MutableList<String>

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
    private var kanjiScreenView: View = layoutInflater.inflate(R.layout.kanji_screen, null)


    init {
        setWindowListeners()
    }

    fun setWordData(
        word: DictionaryModel,
        kanjis: List<KanjiModel>,
        stories: List<KanjiStories>,
        wReviews: MutableList<String>,
        kReviews: MutableList<String>,
    )  {
        currentWord = word
        wordKanjis = kanjis
        kanjiStories = stories
        wordReviews = wReviews
        kanjiReviews = kReviews
    }

    fun openWordDict() {
        bindWordData()
        removeView(loadingScreenView)
        addView(windowDictionaryView, mParams)
    }

    fun openKanjiDict() {
        bindKanjiData()
        removeView(windowDictionaryView)
        addView(kanjiScreenView, mParams)
    }

    fun setLoadingScreen() {
        addView(loadingScreenView, mParams)
    }

    fun addView(view: View, params: WindowManager.LayoutParams) {
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
            Log.d("DICTWINDOWDEBUG", "Error while opening window: $e")
        }
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

    fun setWindowListeners() {
        // Here go the button listeners
        //  this function will be called in the service onCreate
        // to set up all the needed listeners
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

    @SuppressLint("ResourceAsColor")
    private fun bindWordData() {
        if (!currentWord.isEmpty()) {

            val definitions: List<String> = currentWord?.senses?.map {
                val definitions = it?.englishDefinitions ?: listOf()
                val defString = definitions.joinToString(separator=", ")
                defString
            }
            val tags = mutableListOf<String>()
            currentWord.jlpt?.let{
                if(it.isNotEmpty()) tags.add(it)
            }
            if(currentWord.common == true) {
                tags.add("common")
            }
            currentWord.pos?.let{
                if(it.isNotEmpty()) tags.add(it)
            }
            currentWord.dataTags.forEach{
                if(it.isNotEmpty()) tags.add(it)
            }

            val sensesAdapter = SenseItemAdapter().apply {
                submitList(definitions)
            }
            val wordTagsAdapter = WordTagAdapter().apply{
                submitList(tags)
            }

            windowDictionaryView.findViewById<RecyclerView>(R.id.rvSensesList).apply{
                layoutManager = LinearLayoutManager(context)
                adapter = sensesAdapter
            }
            windowDictionaryView.findViewById<RecyclerView>(R.id.rvWordTags).apply{
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = wordTagsAdapter
            }


            windowDictionaryView.findViewById<TextView>(R.id.tvWord).apply{
                text = currentWord.word
                val kanjis = wordKanjis.map{it.kanji}
                for(char in text) {
                    if (kanjis.contains(char.toString())){
                        this.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                        break
                    }
                }
            }

            windowDictionaryView.findViewById<TextView>(R.id.tvFurigana).text = currentWord.reading
            windowDictionaryView.findViewById<TextView>(R.id.tvWord).setOnClickListener {
                openKanjiDict()
            }
            val favoritesButtons = windowDictionaryView.findViewById<ImageView>(R.id.add_to_favorites)
            var isInReview = wordReviews.contains(currentWord.word)
            if(isInReview){
                favoritesButtons.setImageResource(R.drawable.favorites_button_active)
            } else {
                favoritesButtons.setImageResource(R.drawable.favorites_button_inactive)
            }
            favoritesButtons.setOnClickListener{ btn ->
                if(isInReview) {
                    favoritesButtons.setImageResource(R.drawable.favorites_button_inactive)
                    scope.launch{
                        reviewRepo.removeWordReviewFromId(currentWord.word)
                        isInReview = false
                    }
                } else{
                    favoritesButtons.setImageResource(R.drawable.favorites_button_active)
                    scope.launch {
                        reviewRepo.addWordToReview(currentWord)
                        isInReview = true
                    }
                }
            }
        }
    }

    private fun bindKanjiData() {
        val kanjisAdapter = KanjiListAdapter(context, reviewRepo, scope).apply{
            sumbitKanjisData(wordKanjis)
            sumbitStoriesData(kanjiStories.map{it.story})
            sumbitReviewKanjisData(kanjiReviews)
        }
        kanjiScreenView.findViewById<RecyclerView>(R.id.rvKanjisContainer).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = kanjisAdapter
        }
        kanjiScreenView.findViewById<ImageView>(R.id.kanjiClose).setOnClickListener {
            removeView(kanjiScreenView)
            addView(windowDictionaryView, mParams)
        }
    }

    private fun updateView(view: View, params: WindowManager.LayoutParams) {
        overlayWindowManager.updateViewLayout(view, params)
    }
}