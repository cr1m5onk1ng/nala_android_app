package com.example.nala.domain.model.search

data class SemanticSearchModel (
    val query: String,
    val results: List<String>
    )