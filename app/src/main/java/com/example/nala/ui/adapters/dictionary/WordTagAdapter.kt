package com.example.nala.ui.adapters.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.nala.R

class WordTagAdapter : RecyclerView.Adapter<WordTagAdapter.WordTagViewHolder>(){

    private var wordTags = listOf<String>()

    inner class WordTagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordTagView: TextView = itemView.findViewById(R.id.tvWordTag)
        val wordTagContainerView: CardView = itemView.findViewById(R.id.word_tag_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordTagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_tag_item, parent, false)
        return WordTagViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordTagViewHolder, position: Int) {
        holder.wordTagView.text = wordTags[position]
        holder.wordTagContainerView.apply {
            if(position % 3 == 0) {
                setCardBackgroundColor(resources.getColor(R.color.lightBlue))
            } else if(position % 3 == 1) {
                setCardBackgroundColor(resources.getColor(R.color.lightGreen))
            } else if(position % 3 == 2) {
                setCardBackgroundColor(resources.getColor(R.color.lightRed))
            }
        }
    }

    override fun getItemCount(): Int {
        return wordTags.size
    }

    fun submitList(data: List<String>) {
        wordTags = data
    }
}