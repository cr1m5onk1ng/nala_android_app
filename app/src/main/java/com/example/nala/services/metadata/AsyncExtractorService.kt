package com.example.nala.services.metadata

import com.example.nala.domain.model.metadata.MetadataModel

interface AsyncExtractorService<T> {

    suspend fun extractFromUrl(documentUrl: String): T
}