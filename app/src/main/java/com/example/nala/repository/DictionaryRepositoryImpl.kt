package com.example.nala.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nala.db.dao.ReviewDao
import com.example.nala.db.models.review.WordReviewDbDtoMapper
import com.example.nala.db.models.review.WordReviewModelDto
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.util.SmResponse
import com.example.nala.domain.util.SuperMemo2
import com.example.nala.network.model.dictionary.DictionaryModelDtoMapper
import com.example.nala.network.services.DictionaryService
import java.time.LocalDate

class DictionaryRepositoryImpl(
    private val dictionaryService : DictionaryService,
    private val networkMapper : DictionaryModelDtoMapper,
) : DictionaryRepository {

    override suspend fun search(word: String): DictionaryModel {
        val result = dictionaryService.search(word);
        return networkMapper.mapToDomainModel(result);
    }
}