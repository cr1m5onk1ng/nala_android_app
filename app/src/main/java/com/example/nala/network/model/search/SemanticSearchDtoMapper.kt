package com.example.nala.network.model.search

import com.example.nala.domain.model.search.SemanticSearchModel
import com.example.nala.domain.util.DomainMapper

class SemanticSearchDtoMapper : DomainMapper<SemanticSearchApiDto, SemanticSearchModel> {
    override fun mapToDomainModel(model: SemanticSearchApiDto): SemanticSearchModel {
        return SemanticSearchModel(
            query = model.query,
            results = model.results
        )
    }

    override fun mapFromDomainModel(model: SemanticSearchModel): SemanticSearchApiDto {
        return SemanticSearchApiDto(
            query = model.query,
            results = model.results,
        )
    }
}