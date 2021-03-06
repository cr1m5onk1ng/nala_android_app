package com.example.nala.services.metadata

import com.example.nala.domain.model.metadata.MetadataModel

interface ExtractorService<T> {

    fun extractFromUrl(url: String) : T
}