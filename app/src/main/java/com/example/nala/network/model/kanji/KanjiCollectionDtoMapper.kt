package com.example.nala.network.model.kanji

import android.util.Log
import com.example.nala.domain.model.kanji.KanjiCollection
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.util.DomainMapper
import java.util.*

class KanjiCollectionDtoMapper() : DomainMapper<KanjiCollectionDto, KanjiCollection> {
    val kanjiModeDtoMapper = KanjiModelDtoMapper()

    override fun mapToDomainModel(model: KanjiCollectionDto): KanjiCollection {
        val kanjiMap: HashMap<String, KanjiModel> = HashMap()
        model.kanjis.forEach { kanji ->
            kanjiMap[kanji.kanji] = kanjiModeDtoMapper.mapToDomainModel(kanji)
        }
        return KanjiCollection(
            kanjis = kanjiMap
        )
    }

    override fun mapFromDomainModel(domainModel: KanjiCollection): KanjiCollectionDto {
        TODO("Not yet implemented")
    }

}