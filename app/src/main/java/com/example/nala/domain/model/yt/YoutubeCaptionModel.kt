package com.example.nala.domain.model.yt

data class YoutubeCaptionModel(
    val caption: String,
    val start: Float?,
    val duration: Float?,
) {
    companion object {
        fun Empty() : YoutubeCaptionModel {
            return YoutubeCaptionModel(
                caption = "",
                start = null,
                duration = null,
            )
        }
    }

    override fun equals(other: Any?) =
        other is YoutubeCaptionModel &&
                caption == other.caption &&
                start == other.start &&
                duration == other.duration

    override fun hashCode(): Int {
        return caption.hashCode() + start.hashCode()
    }
}