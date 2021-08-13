package com.example.nala.domain.model.yt

data class YoutubeCaptionTracksModel(
    val id: String,
    val name: String,
    val langCode: String,
    val langOriginal: String,
    val langTranslated: String,
    val langDefault: Boolean,
)