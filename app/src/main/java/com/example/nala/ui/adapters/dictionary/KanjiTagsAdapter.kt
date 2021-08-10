package com.example.nala.ui.adapters.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.nala.R

class KanjiTagsAdapter : RecyclerView.Adapter<KanjiTagsAdapter.KanjiTagsViewHolder>() {

    var kanjiTags: List<String> = listOf()

    inner class KanjiTagsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val kanjiTagView = itemView.findViewById<TextView>(R.id.tvKanjiTag)
        val kanjiTagContainerView = itemView.findViewById<CardView>(R.id.kanji_tag_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KanjiTagsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.kanji_tag_item, parent, false)
        return KanjiTagsViewHolder(view)
    }

    override fun onBindViewHolder(holder: KanjiTagsViewHolder, position: Int) {
        holder.kanjiTagView.text = kanjiTags[position]
        holder.kanjiTagContainerView.apply{
            if(position == 0) {
                setCardBackgroundColor(resources.getColor(R.color.lightBlue))
            } else if(position == 1) {
                setCardBackgroundColor(resources.getColor(R.color.lightGreen))
            } else {
                setCardBackgroundColor(resources.getColor(R.color.lightRed))
            }
        }
    }

    override fun getItemCount(): Int {
        return kanjiTags.size
    }

    fun submitList(data: List<String>) {
        kanjiTags = data
    }

}