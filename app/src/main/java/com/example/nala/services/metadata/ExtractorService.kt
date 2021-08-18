package com.example.nala.services.metadata

import com.example.nala.domain.model.metadata.MetadataModel

interface ExtractorService {

    fun extractFromUrl(url: String) : MetadataModel
}