package com.example.nala.db.models.review.mappers

import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.domain.util.DomainMapper

class SentenceReviewDbDtoMapper : DomainMapper<com.example.nala.db.models.review.SentenceReviewCache, SentenceReviewModel> {


    override fun mapToDomainModel(cache: com.example.nala.db.models.review.SentenceReviewCache): SentenceReviewModel {
        return SentenceReviewModel(
            sentence = cache.sentence,
            targetWord = cache.targetWord
        )
    }

    override fun mapFromDomainModel(model: SentenceReviewModel): com.example.nala.db.models.review.SentenceReviewCache {
        return com.example.nala.db.models.review.SentenceReviewCache(
            sentence = model.sentence,
            targetWord = model.targetWord
        )
    }
}