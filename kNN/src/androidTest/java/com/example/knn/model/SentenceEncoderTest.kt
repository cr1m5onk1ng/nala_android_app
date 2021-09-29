package com.example.knn.model

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.example.knn.tokenization.WordPieceTokenizer
import org.junit.Before
import org.junit.Test
import kotlin.system.measureTimeMillis
import com.google.common.truth.Truth.assertThat

class SentenceEncoderTest{

    private lateinit var sentenceEncoder: SentenceEncoder
    private val appContext = ApplicationProvider.getApplicationContext<Context>()
    private val sentence = "This is a test"

    @Before
    fun setup() {
        sentenceEncoder = SentenceEncoder(appContext, "encoder.ptl", WordPieceTokenizer(appContext))
    }

    @Test
    fun isComputationWithinOneSecond() {
        assertThat(sentenceEncoder.model).isNotNull()
        val time = measureTimeMillis {
            val embedding = sentenceEncoder.predict(sentence)

            println("Embeddings shape: $embedding")
        }
        assertThat(time).isAtMost(1000L)
    }
}