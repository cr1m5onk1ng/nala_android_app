package com.example.nala.service.tokenization
import com.atilika.kuromoji.TokenizerBase
import com.atilika.kuromoji.ipadic.Tokenizer

class JapaneseTokenizerService : TokenizerService{
    private val tokenizer = Tokenizer.Builder().mode(TokenizerBase.Mode.NORMAL).build()

    override suspend fun tokenize(text: String): List<String> {
        val tokens = tokenizer.tokenize(text)
        return tokens.map {
            it.surface
        }
    }
}