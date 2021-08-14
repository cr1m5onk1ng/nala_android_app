package com.example.nala.network.model.yt.captions

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name="transcript", strict=false)
data class CaptionsList @JvmOverloads constructor (
    @param:ElementList(entry = "text", inline = true)
    @get:ElementList(entry = "text", inline = true)
    val captions: List<CaptionData>? = null,
)