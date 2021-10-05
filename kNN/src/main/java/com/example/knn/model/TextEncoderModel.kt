package com.example.knn.model

interface TextEncoderModel {
    fun predict(text: String) : FloatArray
}