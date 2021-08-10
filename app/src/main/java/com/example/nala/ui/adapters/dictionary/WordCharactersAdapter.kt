package com.example.nala.ui.adapters.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nala.R

class WordCharactersAdapter : RecyclerView.Adapter<WordCharactersAdapter.WordCharachtersViewHolder>() {

    private var chars: List<String> = listOf()

    inner class WordCharachtersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val charView: TextView = itemView.findViewById(R.id.tvWordChar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordCharachtersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_character, parent, false)
        return WordCharachtersViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordCharachtersViewHolder, position: Int) {
        holder.charView.text = chars[position]
    }

    override fun getItemCount(): Int {
        return chars.size
    }

    fun submitList(data: List<String>) {
        chars = data
    }
}