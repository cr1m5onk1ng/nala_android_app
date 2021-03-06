package com.example.nala.ui.adapters.dictionary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nala.R
import com.example.nala.domain.model.kanji.KanjiModel
import it.mike5v.viewmoretextview.ViewMoreTextView

class KanjiListAdapterV2 (
    private val context: Context,
    private val onAddKanji: (KanjiModel) -> Unit,
) : RecyclerView.Adapter<KanjiListAdapterV2.KanjiListViewHolder>() {

        private var kanjis = listOf<KanjiModel>()
        private var stories = listOf<String>()

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
            holder.kanji.text = kanjis[position].kanji
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
            holder.kanjiMeanings.text = kanjis[position].meaning?.joinToString(separator=", ")
            holder.kanjiOns.text = kanjis[position].onReadings?.joinToString(separator=", ")
            holder.kanjiKun.text = kanjis[position].kunReadings?.joinToString(separator=", ")
            val currentKanji = kanjis[position]
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
            holder.addKanjiToFavorites.setOnClickListener {
                onAddKanji(kanjis[position])
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
}