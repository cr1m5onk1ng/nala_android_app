package com.example.nala.repository

import android.util.Log
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.network.model.dictionary.DictionaryModelDtoMapper
import com.example.nala.network.services.DictionaryService
import com.example.nala.services.tokenization.JapaneseTokenizerService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DictionaryRepositoryImpl @Inject constructor(
    private val dictionaryService : DictionaryService,
    private val tokenizerService: JapaneseTokenizerService,
    private val dictionaryModelMapper : DictionaryModelDtoMapper,

    ) : DictionaryRepository {

    override suspend fun search(word: String): DictionaryModel {
        try {
            val result = dictionaryService.search(word)
            return dictionaryModelMapper.mapToDomainModel(result)
        } catch (e: Exception){
            e.printStackTrace()
        }
        return DictionaryModel.Empty()
    }

    override fun searchFlow(word: String): Flow<DictionaryModel> {
        try {
            return flow {
                dictionaryService.search(word)
            }
        } catch(e: Exception){
            e.printStackTrace()
        }
        return flow { DictionaryModel.Empty() }
    }

    override suspend fun tokenize(text: String) : List<String> {
        return tokenizerService.tokenize(text)
    }

    override fun tokensToIndexMap(tokens: List<String>, text: String): Map<Pair<Int, Int>, String> {
        val indexedTokens = HashMap<Pair<Int, Int>, String>()
        var index = 0
        Log.d("TOKENIZATIONDEBUG", "Tokens: $tokens ")
        for (token in tokens) {
            val tokenLen = token.length
            val offset = (index + tokenLen) - 1
            indexedTokens[Pair(index, offset)] = token
            index = offset + 1
        }
        return indexedTokens
    }
}