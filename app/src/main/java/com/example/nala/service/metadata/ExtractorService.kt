package com.example.nala.service.metadata

import com.example.nala.domain.model.metadata.MetadataModel

interface ExtractorService {

    suspend fun extractFromUrl(url: String) : MetadataModel
}