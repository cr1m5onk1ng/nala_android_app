package com.example.nala.db.models.review.mappers

import com.example.nala.db.models.review.*
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.dictionary.Sense
import com.example.nala.domain.util.DomainMapper
import java.util.*

class WordReviewDbDtoMapper : DomainMapper<WordReviewModel, DictionaryModel> {


    override fun mapFromDomainModel(model: DictionaryModel): WordReviewModel {

        return WordReviewModel(
            word = model.word,
            reading = model.reading,
            jlpt = model.jlpt,
            common = model.common,
            pos = model.pos,
        )
    }

    fun mapDefinitionsFromDomainModel(word: String, definitions: List<String>) : List<WordDefinition> {
        return definitions.map {
            WordDefinition(
                word = word,
                definition = it
            )
        }
    }

    fun mapSenseFromDomainModel(sense: Sense, word: String) : WordSenseDb {
        return WordSenseDb(
            senseId = UUID.randomUUID().toString(),
            word = word,
            pos = sense?.partsOfSpeech?.first() ?: ""
        )
    }

    fun mapTagsFromDomainModel(word: String, tags: List<String>) : List<WordTag> {
        return tags.map {
            WordTag(
                word = word,
                tag = it
            )
        }
    }

    fun mapSenseTagsFromDomainModel(tags: List<String>, senseId: String) : List<WordSenseTagDb> {
        return tags.map {
            WordSenseTagDb(
                senseId = senseId,
                tag = it
            )
        }
    }

    fun mapSenseDefinitionsFromDomainModel(definitions: List<String>, senseId: String) : List<WordSenseDefinitionDb> {
        return definitions.map {
            WordSenseDefinitionDb(
                senseId = senseId,
                definition = it
            )
        }
    }

    fun mapSensesFromDomainModel(senses: List<Sense>, word: String) : List<WordSenseDb> {
        return senses.map {
            WordSenseDb(
                senseId = UUID.randomUUID().toString(),
                word = word,
                pos = it.partsOfSpeech?.first() ?: "",
            )
        }
    }

    override fun mapToDomainModel(model: WordReviewModel): DictionaryModel {
        TODO("Not yet implemented")
    }


}