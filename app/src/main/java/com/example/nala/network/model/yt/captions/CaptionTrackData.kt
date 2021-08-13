package com.example.nala.network.model.yt.captions

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name="track", strict=false)
data class CaptionTrackData @JvmOverloads constructor(
    @param:Attribute(name = "id", required = false)
    @get:Attribute(name = "id", required = false)
    val id: String,

    @param:Attribute(name = "name", required = false)
    @get:Attribute(name = "name", required = false)
    val name: String,

    @param:Attribute(name = "lang_code", required = false)
    @get:Attribute(name = "lang_code", required = false)
    val langCode: String,

    @param:Attribute(name = "lang_original", required = false)
    @get:Attribute(name = "lang_original", required = false)
    val langOriginal: String,

    @param:Attribute(name = "lang_translated", required = false)
    @get:Attribute(name = "lang_translated", required = false)
    val langTranslated: String,

    @param:Attribute(name = "lang_default", required = false, empty="false")
    @get:Attribute(name = "lang_default", required = false,  empty="false")
    val langDefault: String,
)