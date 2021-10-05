package com.example.knn.classifier

import org.junit.Before
import org.junit.Test
import org.pytorch.Tensor
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import com.google.common.truth.Truth.assertThat

class SimpleKNNClassifierTest {

    private lateinit var classifier: SimpleKNNClassifier
    private lateinit var vectors: Array<FloatArray>
    private lateinit var query: FloatArray
    private val dim = 384
    private val numVectors = 500
    private val k = 10

    @Before
    fun setup() {
        classifier = SimpleKNNClassifier(CosineDistance())
        val queryFloatBuffer = Tensor.allocateFloatBuffer(dim)
        for(i in 0 until dim) {
            queryFloatBuffer.put(Random.nextFloat())
        }
        query = Tensor.fromBlob(queryFloatBuffer, longArrayOf(dim.toLong())).dataAsFloatArray
        val uninitializedVectors = Array(numVectors, init = { floatArrayOf() })
        for (i in 0 until numVectors) {
            val floatBlob = Tensor.allocateFloatBuffer(dim)
            for(j in 0 until dim) {
                floatBlob.put(Random.nextFloat())
            }
            val arr = Tensor.fromBlob(floatBlob, longArrayOf(dim.toLong())).dataAsFloatArray
            uninitializedVectors[i] = arr
        }
        vectors = uninitializedVectors
    }

    @Test
    fun `classifier under one second`() {
        val time = measureTimeMillis {
            val topK = classifier.getTopK(query, vectors, k)
            println("Top k: $topK")
        }
        assertThat(time).isAtMost(1000L)
        println("Time: $time")

    }
}