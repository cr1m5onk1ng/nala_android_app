package com.example.nala.network.model.dictionary

import com.example.nala.domain.model.dictionary.Japanese
import com.example.nala.domain.util.DomainMapper

class JapaneseDtoMapper : DomainMapper<JapaneseDto, Japanese> {
    override fun mapToDomainModel(model: JapaneseDto): Japanese {
        return Japanese(
            reading = model.reading,
            word = model.word,
        )
    }

    fun mapJapaneseListToDomainModel(japaneseList: List<JapaneseDto>) : List<Japanese>{
        return japaneseList.map {
            mapToDomainModel(it)
        }
    }

    override fun mapFromDomainModel(domainModel: Japanese): JapaneseDto {
        TODO("Not yet implemented")
    }
}