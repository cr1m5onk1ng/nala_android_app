package com.example.nala.services.tokenization
import com.atilika.kuromoji.TokenizerBase
import com.atilika.kuromoji.ipadic.Tokenizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JapaneseTokenizerService @Inject constructor(): TokenizerService{
    private val tokenizer = Tokenizer.Builder().mode(TokenizerBase.Mode.NORMAL).build()

    override suspend fun tokenize(text: String): List<String> {
        val tokens = tokenizer.tokenize(text)
        return tokens.map {
            it.surface
        }
    }
}