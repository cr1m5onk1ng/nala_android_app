package com.example.nala.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nala.R

class WordTagAdapter : RecyclerView.Adapter<WordTagAdapter.WordTagViewHolder>(){

    private var wordTags = listOf<String>()

    inner class WordTagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordTagView: TextView = itemView.findViewById(R.id.tvWordTag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordTagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_tag_item, parent, false)
        return WordTagViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordTagViewHolder, position: Int) {
        holder.wordTagView.text = wordTags[position]
    }

    override fun getItemCount(): Int {
        return wordTags.size
    }

    fun submitList(data: List<String>) {
        wordTags = data
    }
}