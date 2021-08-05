package com.example.nala.domain.model.dictionary

import android.os.Parcelable
import com.example.nala.domain.model.DomainModel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Sense(
    val antonyms: @RawValue List<Any>? = null,
    val englishDefinitions: List<String>? = null,
    val info: @RawValue List<Any>? = null,
    val links: @RawValue List<Link>? = null,
    val partsOfSpeech: List<String>? = null,
    val restrictions: @RawValue List<Any>? = null,
    val seeAlso: @RawValue List<Any>? = null,
    val sentences: @RawValue List<Any>? = null,
    val source: @RawValue List<Any>? = null,
    val tags: List<String>? = null,
) : DomainModel(), Parcelable