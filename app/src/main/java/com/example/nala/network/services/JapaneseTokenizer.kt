package com.example.nala.network.services
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.optimized.DefaultTermFeatures

class JapaneseTokenizer(
    val tokenizer: Tokenizer<DefaultTermFeatures>
) : TokenizerService {
    override suspend fun tokenize(word: String): List<String> {
        return tokenizer.tokenize(word).map { it.text }
    }
}