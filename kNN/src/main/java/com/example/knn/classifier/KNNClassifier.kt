package com.example.knn.classifier

interface KNNClassifier<T> {
    fun getTopK(query: T, vectors: Array<T>, topK: Int) : List<KNNResult>
}