package com.example.nala.network.services

import com.example.nala.network.model.search.SearchApiDto
import retrofit2.http.Body
import retrofit2.http.POST

interface SearchApiService {
    @POST("search")
    suspend fun searchSentences(@Body sentence: String) : SearchApiDto

}