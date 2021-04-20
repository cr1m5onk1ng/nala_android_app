package com.example.nala.db.models.review.mappers

import com.example.nala.db.models.review.SentenceReviewModelDto
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.domain.util.DomainMapper

class SentenceReviewDbDtoMapper : DomainMapper<SentenceReviewModelDto, SentenceReviewModel> {


    override fun mapToDomainModel(model: SentenceReviewModelDto): SentenceReviewModel {
        return SentenceReviewModel(
            sentence = model.sentence,
            targetWord = model.targetWord
        )
    }

    override fun mapFromDomainModel(model: SentenceReviewModel): SentenceReviewModelDto {
        return SentenceReviewModelDto(
            sentence = model.sentence,
            targetWord = model.targetWord
        )
    }
}