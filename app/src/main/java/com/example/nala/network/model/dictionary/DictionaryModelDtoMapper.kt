package com.example.nala.network.model.dictionary

import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.util.DomainMapper

class DictionaryModelDtoMapper : DomainMapper <DictionaryModelDto, DictionaryModel>  {
    private val dataDtoMapper = DataDtoMapper()

    private val senseDtoMapper = SenseDtoMapper()

    override fun mapToDomainModel(model: DictionaryModelDto): DictionaryModel {
        val data = dataDtoMapper.mapDataListToDomainModel(model.data)
        if (data.isEmpty())
            return DictionaryModel.Empty()
        val senses = senseDtoMapper.mapSensesToDomainModel(model.data.first().senses)
        val meta = model.meta
        var word = data.first().slug ?: ""
        val japanese = data.first().japanese?.first()
        val reading = japanese?.reading ?: ""
        val japaneseWord = japanese?.word ?: ""
        if(word.contains(Regex("[0-9]"))) word = japaneseWord
        val jlptList = data.first()?.jlpt ?: listOf()
        val jlptString: String = if(jlptList?.isEmpty()) "" else jlptList?.first()
        val pos = data.first().senses?.first()?.partsOfSpeech?.first() ?: ""
        val common = data.first().isCommon
        //val definitions = sense?.englishDefinitions ?: listOf()
        val tags = data.first().tags ?: listOf()
        //val sensesTags = data.first()?.senses?.first()?.tags ?: listOf()
        return DictionaryModel(
            word = word,
            reading = reading,
            jlpt = jlptString,
            pos = pos,
            common = common,
            dataTags = tags,
            senses = senses,
        )
    }

    override fun mapFromDomainModel(domainModel: DictionaryModel): DictionaryModelDto {
        TODO("Not yet implemented")
    }


}