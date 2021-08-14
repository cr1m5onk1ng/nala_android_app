package com.example.nala.network.model.yt.captions

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name="transcript_list", strict=false)
data class CaptionTracksList @JvmOverloads constructor(

    @param:Attribute(name="docid", required=false)
    @get:Attribute(name="docid", required=false)
    val docid: String? = null,

    @param:ElementList(entry = "track", inline = true, required = false)
    @get:ElementList(entry = "track", inline = true, required = false)
    val tracks: List<CaptionTrackData>? = null,
)