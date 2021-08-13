package com.example.nala.network.model.yt.captions

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name="transcript_list")
data class CaptionTracksList @JvmOverloads constructor(
    @param:ElementList(entry = "track", inline = true)
    @get:ElementList(entry = "track", inline = true)
    val tracks: List<CaptionTrackData>,
)