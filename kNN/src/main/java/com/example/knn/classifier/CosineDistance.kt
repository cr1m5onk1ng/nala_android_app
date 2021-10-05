package com.example.knn.classifier

import kotlin.math.sqrt

class CosineDistance : DistanceMetric<FloatArray, Float> {
    override fun distance(vectorA: FloatArray, vectorB: FloatArray) : Float{
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f
        for(i in vectorA.indices) {
            dotProduct += vectorA[i] * vectorB[i]
            normA += vectorA[i] * vectorA[i]
            normB += vectorB[i] * vectorB[i]
        }
        return dotProduct / (sqrt(normA) + sqrt(normB))
    }
}