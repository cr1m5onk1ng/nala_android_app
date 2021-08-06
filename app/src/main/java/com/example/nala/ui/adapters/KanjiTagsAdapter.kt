package com.example.nala.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nala.R

class KanjiTagsAdapter : RecyclerView.Adapter<KanjiTagsAdapter.KanjiTagsViewHolder>() {

    var kanjiTags: List<String> = listOf()

    inner class KanjiTagsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val kanjiTagView = itemView.findViewById<TextView>(R.id.tvKanjiTag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KanjiTagsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.kanji_tag_item, parent, false)
        return KanjiTagsViewHolder(view)
    }

    override fun onBindViewHolder(holder: KanjiTagsViewHolder, position: Int) {
        holder.kanjiTagView.text = kanjiTags[position]
    }

    override fun getItemCount(): Int {
        return kanjiTags.size
    }

    fun submitList(data: List<String>) {
        kanjiTags = data
    }

}