package com.example.knn.classifier

class SimpleKNNClassifier(
    private val distanceMetric: DistanceMetric<FloatArray, Float>,
) : KNNClassifier<FloatArray> {
    override fun getTopK(query: FloatArray, vectors: Array<FloatArray>, topK: Int) : List<KNNResult> {
        assert(topK < vectors.size)
        val scores = getScores(query, vectors)
        return getTopKScores(topK, scores)
    }

    private fun getScores(query: FloatArray, vectors: Array<FloatArray>) : Array<KNNResult> {
        val scores = Array(size = vectors.size, init = { KNNResult(index = 0, score = 0f) })
        for(i in vectors.indices) {
            val distance = distanceMetric.distance(query, vectors[i])
            scores[i] = KNNResult(index = i, score = distance)
        }
        return scores
    }

    // returns the indices of the highest scoring vectors in descending order
    private fun getTopKScores(topK: Int, scores: Array<KNNResult>) : List<KNNResult> {
        return scores
            .sortedWith(comparator = { t, t2 -> - t.score.compareTo(t2.score) })
            .take(topK)
    }
}