package com.example.nala.ui.adapters.dictionary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nala.R
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.repository.ReviewRepository
import it.mike5v.viewmoretextview.ViewMoreTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class KanjiListAdapter(
    private val context: Context,
    private val reviewRepository: ReviewRepository,
    private val scope: CoroutineScope,
    ) : RecyclerView.Adapter<KanjiListAdapter.KanjiListViewHolder>() {

    private var kanjis = listOf<KanjiModel>()
    private var stories = listOf<String>()
    private var reviewKanjis = listOf<String>()

    inner class KanjiListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val kanji = itemView.findViewById<TextView>(R.id.tvKanji)
        val story = itemView.findViewById<ViewMoreTextView>(R.id.tvKanjiStory)
        val kanjiMeanings = itemView.findViewById<TextView>(R.id.tvKanjiMeanings)
        val kanjiOns = itemView.findViewById<TextView>(R.id.tvKanjiOn)
        val kanjiKun = itemView.findViewById<TextView>(R.id.tvKanjiKun)
        val kanjiTags = itemView.findViewById<RecyclerView>(R.id.rvKanjiTags)
        val addKanjiToFavorites = itemView.findViewById<ImageView>(R.id.addKanjiToFavorites)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KanjiListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.kanji_detail_item, parent, false)
        return KanjiListViewHolder(view)
    }

    override fun onBindViewHolder(holder: KanjiListViewHolder, position: Int) {
        val kanji = kanjis[position]
        holder.kanji.text = kanji.kanji
        holder.story.text = stories[position]
        holder.story.apply{
            setAnimationDuration(200)
                .setVisibleLines(3)
                .setIsExpanded(false)
                .setEllipsizedTextColor(ContextCompat.getColor(context, R.color.blue700))
            setOnClickListener {
                toggle()
            }
        }
        holder.kanjiMeanings.text = kanji.meaning?.joinToString(separator=", ")
        if(kanji.onReadings == null || kanji.onReadings.isEmpty()) {
            holder.kanjiOns.visibility = GONE
        } else {
            holder.kanjiOns.text = kanji.onReadings?.joinToString(separator=", ")
        }
        if(kanji.kunReadings == null || kanji.kunReadings.isEmpty()){
            holder.kanjiKun.visibility = GONE
        } else {
            holder.kanjiKun.text = kanji.kunReadings?.joinToString(separator=", ")
        }
        val currentKanji = kanji
        var tags = mutableListOf<String>()
        currentKanji.jlpt?.let{
            tags.add("jlpt-"+it)
        }
        currentKanji.freq?.let{
            tags.add("freq: "+it)
        }
        currentKanji.grade?.let{
            tags.add("grade: "+it)
        }

        val tagsAdapter = KanjiTagsAdapter().apply {
            submitList(tags)
        }
        holder.kanjiTags.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = tagsAdapter
        }

        var isInReview = reviewKanjis.contains(kanji.kanji)

        if(isInReview){
            holder.addKanjiToFavorites.setImageResource(R.drawable.favorites_button_active)
        } else {
            holder.addKanjiToFavorites.setImageResource(R.drawable.favorites_button_inactive)
        }
        holder.addKanjiToFavorites.setOnClickListener {
            if(isInReview) {
                holder.addKanjiToFavorites.setImageResource(R.drawable.favorites_button_inactive)
                scope.launch {
                    reviewRepository.removeKanjiReviewItemFromId(kanji.kanji)
                    isInReview = false
                }
            }else{
                holder.addKanjiToFavorites.setImageResource(R.drawable.favorites_button_active)
                scope.launch {
                    reviewRepository.addKanjiToReview(kanji)
                    isInReview = true
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return kanjis.size
    }

    fun sumbitKanjisData(kanjisData: List<KanjiModel>) {
        kanjis = kanjisData
    }

    fun sumbitStoriesData(storiesData: List<String>) {
        stories = storiesData
    }

    fun sumbitReviewKanjisData(kanjisData: List<String>) {
        reviewKanjis = kanjisData
    }
}