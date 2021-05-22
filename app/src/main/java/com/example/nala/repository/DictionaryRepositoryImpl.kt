package com.example.nala.repository

import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.network.model.dictionary.DictionaryModelDtoMapper
import com.example.nala.network.model.dictionary.SearchOutput
import com.example.nala.network.model.dictionary.SenseDtoMapper
import com.example.nala.network.services.DictionaryService
import com.example.nala.service.tokenization.JapaneseTokenizerService
import javax.inject.Inject

class DictionaryRepositoryImpl @Inject constructor(
    private val dictionaryService : DictionaryService,
    private val tokenizerService: JapaneseTokenizerService,
    private val dictionaryModelMapper : DictionaryModelDtoMapper,

    ) : DictionaryRepository {

    override suspend fun search(word: String): DictionaryModel {
        try {
            val result = dictionaryService.search(word);
            return dictionaryModelMapper.mapToDomainModel(result);
        } catch (e: Exception){
            e.printStackTrace()
        }
        return DictionaryModel.Empty()
    }

    override suspend fun tokenize(text: String) : List<String> {
        return tokenizerService.tokenize(text)
    }
}