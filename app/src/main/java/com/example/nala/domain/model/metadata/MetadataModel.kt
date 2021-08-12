package com.example.nala.domain.model.metadata

data class MetadataModel(
    val title: String,
    val description: String? = null,
    val thumbnailUrl: String? = null,
) {
    companion object{
        fun Empty() : MetadataModel {
            return MetadataModel(
                title = "",
            )
        }
    }
}