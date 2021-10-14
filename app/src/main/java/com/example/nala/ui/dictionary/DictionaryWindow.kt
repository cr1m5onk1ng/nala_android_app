package com.example.nala.ui.dictionary

import android.annotation.SuppressLint
import android.content.Context.WINDOW_SERVICE
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
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
import androidx.core.content.ContextCompat.startActivity
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

    private var currentWord: DictionaryModel = DictionaryModel.Empty()
    private var wordKanjis: List<KanjiModel> = listOf()
    private var kanjiStories: List<KanjiStories> = listOf()
    private var wordReviews: MutableList<String> = mutableListOf()
    private var kanjiReviews: MutableList<String> = mutableListOf()

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
    private var errorScreenView: View = layoutInflater.inflate(R.layout.word_not_found_screen, null)


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
        removeView(windowDictionaryView)
        addView(loadingScreenView, mParams)
        bindKanjiData()
        removeView(loadingScreenView)
        addView(kanjiScreenView, mParams)
    }

    fun setLoadingScreen() {
        addView(loadingScreenView, mParams)
    }

    fun setErrorScreen() {
        addView(errorScreenView, mParams)
    }

    fun closeKanjiView() {
        try {
            // remove the view from the window
            (context.getSystemService(WINDOW_SERVICE) as WindowManager).removeView(kanjiScreenView)
            // invalidate the view
            kanjiScreenView.invalidate()
            // remove all views
            //(kanjiScreenView.parent as ViewGroup).removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("DICTWINDOWDEBUG", "Error while closing window: $e")
        }
    }

    fun closeErrorView() {
        try {
            // remove the view from the window
            (context.getSystemService(WINDOW_SERVICE) as WindowManager).removeView(errorScreenView)
            // invalidate the view
            errorScreenView.invalidate()
            // remove all views
            //(errorScreenView.parent as ViewGroup).removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("DICTWINDOWDEBUG", "Error while closing window: $e")
        }
    }

    fun closeDictionaryView() {
        try {
            // remove the view from the window
            (context.getSystemService(WINDOW_SERVICE) as WindowManager).removeView(windowDictionaryView)
            // invalidate the view
            windowDictionaryView.invalidate()
            // remove all views
            //(windowDictionaryView.parent as ViewGroup).removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("DICTWINDOWDEBUG", "Error while closing window: $e")
        }
    }

    private fun addView(view: View, params: WindowManager.LayoutParams) {
        try {
            overlayWindowManager.addView(view, params)
        } catch (e: Exception) {
            Log.d("DICTWINDOWDEBUG", "Error while opening window: $e")
        }
    }

    private fun removeView(view: View) {
        try {
            // remove the view from the window
            (context.getSystemService(WINDOW_SERVICE) as WindowManager).removeView(view)
            // invalidate the view
            view.invalidate()
        } catch (e: Exception) {
            Log.d("DICTWINDOWDEBUG", "Error while closing window: $e")
        }
    }

    private fun setWindowListeners() {
        /*
        windowDictionaryView.setOnTouchListener { _, event ->
            var downX = 0
            var downY = 0
            var touchDownX = 0f
            var touchDownY = 0f

            when(event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = mParams.x
                    downY = mParams.y
                    touchDownX = event.rawX
                    touchDownY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    mParams.x = (downX + event.rawX - touchDownX).toInt()
                    mParams.y = (downY + event.rawY - touchDownY).toInt()
                    overlayWindowManager.updateViewLayout(windowDictionaryView, mParams)
                }
                else -> {

                }
            }
            true
        } */
        windowDictionaryView.findViewById<ImageView>(R.id.window_close).apply{
            setOnClickListener{
                closeDictionaryView()
            }
        }
        windowDictionaryView.findViewById<ImageView>(R.id.share_word).apply{
            //startShareIntent(currentWord.word)
        }
        kanjiScreenView.findViewById<ImageView>(R.id.kanjiClose).setOnClickListener{
            closeKanjiView()
        }
        errorScreenView.findViewById<ImageView>(R.id.errorClose).setOnClickListener{
            closeErrorView()
        }
        // TODO Add draggable listener
        mParams.gravity = Gravity.CENTER
        mParams.x = 0
        mParams.y = 0
    }


    @SuppressLint("ResourceAsColor")
    private fun bindWordData() {
        if (!currentWord.isEmpty()) {

            val definitions: List<String> = currentWord.senses.map {
                val definitions = it.englishDefinitions ?: listOf()
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
            favoritesButtons.setOnClickListener{
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

    private fun startShareIntent(text: String?) {
        text?.let{
            val shareIntent = Intent().apply{
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, it)
                type = "text/plain"
            }
            startActivity(
                context,
                Intent.createChooser(shareIntent,
                    "Share word/sentence to..."
                ),
                null,
            )
        }
    }
}