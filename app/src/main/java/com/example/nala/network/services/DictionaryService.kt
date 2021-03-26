package com.example.nala.network.services

import com.example.nala.network.model.dictionary.DictionaryModelDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DictionaryService {
    @GET("words")
    suspend fun search(
        @Query("keyword") keyword: String
    ) : DictionaryModelDto
}