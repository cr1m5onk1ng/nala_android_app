package com.example.nala.domain.model.kanji

import com.example.nala.domain.model.DomainModel

data class StoryModel (
    val kanji: String,
    val story: String
    ) : DomainModel() {
    companion object {
        fun Empty() : StoryModel{
           return StoryModel(
               kanji = "",
               story = "",
           )
        }
    }
}