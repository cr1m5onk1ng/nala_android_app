package com.example.nala.domain.model.search

data class SemanticSearchModel (
    val query: String,
    val category: String,
    val results: List<String>
    )