package com.example.nala.db.models.review

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nala.db.models.DatabaseModel
import com.example.nala.domain.model.dictionary.Data
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.dictionary.Japanese
import com.example.nala.domain.model.dictionary.Sense
import com.example.nala.domain.util.DomainMapper
import java.time.LocalDate

class WordReviewDbDtoMapper : DomainMapper<DatabaseModel, DictionaryModel> {

    private val definitionsMapper = WordDefinitionDtoMapper()

    private val tagsMapper = WordTagDtoMapper()

    fun mapReviewToDomainModel (
        wordModel: WordReviewModelDto,
        definitionsModel:WordAndDefinition,
        tagsModel: WordAndTag
    ) : DictionaryModel{
        val definitions = definitionsMapper.mapDefinitionsToDomainModel(definitionsModel)
        val tags = tagsMapper.mapTagsToDomainModel(tagsModel)
        val japanese = Japanese(
            reading = wordModel.reading,
            word = wordModel.word
        )
        val jlpt = wordModel?.jlpt ?: ""
        val pos = wordModel?.pos ?: ""
        val sense = Sense(
            englishDefinitions = definitions,
            partsOfSpeech = if(pos.isNotEmpty()) listOf(pos) else listOf(),
            tags = tags
        )
        val data = Data(
            isCommon = wordModel.common,
            japanese = listOf(japanese),
            jlpt = if(jlpt.isNotEmpty()) listOf(jlpt) else listOf(),
            senses = listOf(sense),
            slug = wordModel.word,
            tags = listOf()
        )
        return DictionaryModel(
            data = listOf(data)
        )
    }

    override fun mapToDomainModel(model: DatabaseModel): DictionaryModel {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun mapFromDomainModel(domainModel: DictionaryModel): WordReviewModelDto {
        val data = domainModel.data?.first()
        var word = data.slug ?: ""
        val japanese = data?.japanese?.first()
        val reading = japanese?.reading ?: ""
        val japaneseWord = japanese?.word ?: ""
        if(word.contains(Regex("[0-9]"))) word = japaneseWord
        val jlpt = data?.jlpt ?: listOf()
        val jlptString: String = if(jlpt.isEmpty()) "" else jlpt.first()
        val pos = data?.senses?.first()?.partsOfSpeech?.first() ?: ""

        return WordReviewModelDto(
            word = word,
            reading = reading,
            jlpt = jlptString,
            common = data?.isCommon ?: false,
            pos = pos,
            scheduledDate = LocalDate.now().toString()
        )
    }

    fun mapDefinitionsFromDomainModel(word: String, definitions: List<String>) : List<WordDefinitionDto> {
        return definitions.map {
            WordDefinitionDto(
                word = word,
                definition = it
            )
        }
    }

    fun mapTagsFromDomainModel(word: String, tags: List<String>) : List<WordTagDto> {
        return tags.map {
            WordTagDto(
                word = word,
                tag = it
            )
        }
    }

}