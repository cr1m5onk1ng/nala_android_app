package com.example.nala.network.model.dictionary

import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.util.DomainMapper

class DictionaryModelDtoMapper : DomainMapper <DictionaryModelDto, DictionaryModel>  {
    val dataDtoMapper = DataDtoMapper()

    override fun mapToDomainModel(model: DictionaryModelDto): DictionaryModel {
        return DictionaryModel(
            data = dataDtoMapper.mapDataListToDomainModel(model.data),
            meta = model.meta
        )
    }

    override fun mapFromDomainModel(domainModel: DictionaryModel): DictionaryModelDto {
        TODO("Not yet implemented")
    }


}