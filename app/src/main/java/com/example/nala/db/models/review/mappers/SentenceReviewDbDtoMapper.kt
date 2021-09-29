package com.example.nala.db.models.review.mappers

import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.domain.util.DomainMapper

class SentenceReviewDbDtoMapper : DomainMapper<com.example.nala.db.models.review.SentenceReviewModel, SentenceReviewModel> {


    override fun mapToDomainModel(model: com.example.nala.db.models.review.SentenceReviewModel): SentenceReviewModel {
        return SentenceReviewModel(
            sentence = model.sentence,
            targetWord = model.targetWord
        )
    }

    override fun mapFromDomainModel(model: SentenceReviewModel): com.example.nala.db.models.review.SentenceReviewModel {
        return com.example.nala.db.models.review.SentenceReviewModel(
            sentence = model.sentence,
            targetWord = model.targetWord
        )
    }
}