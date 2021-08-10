package com.example.nala.network.model.yt.captions

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(name="text", strict=false)
data class CaptionData @JvmOverloads constructor(
        @param:Text
        @get:Text
        val content: String,

        @param:Attribute(name = "start", required = false)
        @get:Attribute(name = "start", required = false)
        val start: Float,

        @param:Attribute(name = "dur", required = false)
        @get:Attribute(name = "dur", required = false)
        val dur: Float,
)