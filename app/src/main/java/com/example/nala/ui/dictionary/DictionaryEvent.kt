package com.example.nala.ui.dictionary

sealed class DictionaryEvent {
    object SearchWordEvent : DictionaryEvent()

    object AddReviewEvent : DictionaryEvent()

    object LoadReviewsEvent: DictionaryEvent()

    object RestoreStateEvent: DictionaryEvent()
}