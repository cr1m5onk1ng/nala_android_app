package com.example.nala.services.classifier

data class ClassifierParameters (
    val MODEL_INPUT_LENGTH: Int,
    val EXTRA_ID_NUM: Int,
    val SEP: String,
    val CLS: String,
        )