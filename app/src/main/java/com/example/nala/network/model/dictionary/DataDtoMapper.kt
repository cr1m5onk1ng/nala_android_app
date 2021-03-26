package com.example.nala.network.model.dictionary

import com.example.nala.domain.model.dictionary.Data
import com.example.nala.domain.util.DomainMapper

class DataDtoMapper : DomainMapper<DataDto, Data> {
    val sensesMapper = SenseDtoMapper()
    val japaneseDtoMapper = JapaneseDtoMapper()

    override fun mapToDomainModel(
        model: DataDto,
    ): Data {
        return Data(
            attribution = null,
            isCommon = model.isCommon,
            japanese = japaneseDtoMapper.mapJapaneseListToDomainModel(model.japanese),
            jlpt = model.jlpt,
            senses = sensesMapper.mapSensesToDomainModel(model.senses),
            slug = model.slug,
            tags = model.tags,
        )
    }

    fun mapDataListToDomainModel(dataList: List<DataDto>) : List<Data> {
        return dataList.map {
            mapToDomainModel(it)
        }
    }

    override fun mapFromDomainModel(domainModel: Data): DataDto {
        TODO("Not yet implemented")
    }
}