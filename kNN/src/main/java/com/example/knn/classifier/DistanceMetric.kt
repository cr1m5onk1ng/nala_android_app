package com.example.knn.classifier

interface DistanceMetric<T, R> {
    fun distance(vectorA: T, vectorB: T) : R
}