package com.example.nala.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nala.R
import com.example.nala.domain.model.dictionary.Sense

class SenseItemAdapter : RecyclerView.Adapter<SenseItemAdapter.SenseItemViewHolder>(){

    private var senses: List<String> = ArrayList()

    class SenseItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senseDefView: TextView = itemView.findViewById(R.id.tvDefinition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SenseItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sense_card, parent, false)
        return SenseItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SenseItemViewHolder, position: Int) {
        holder.senseDefView.text = (position+1).toString() + "." + " " +  senses[position]
    }

    override fun getItemCount(): Int {
        return senses.size
    }

    fun submitList(data: List<String>) {
        senses = data
    }
}