package com.example.nala.service.metadata

import android.util.Log
import com.example.nala.domain.model.metadata.MetadataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class MetadataExtractorService : ExtractorService {

    override fun extractFromUrl(url: String) : MetadataModel {

        val doc = Jsoup.connect(url).get()
        try{
            var imageUrl = ""
            var title = ""
            var description = ""
            val metaOgImage  = doc.select("meta[property=og:image]").first()?.attr("content")
            val metaOgTile = doc.select("meta[property=og:title]").first()?.attr("content")
            val metaOgDescription = doc.select("meta[property=og:description]").first()?.attr("content")
            val descriptionAlt = doc.select("meta[property=description]").first()?.attr("content")
            title = metaOgTile ?: doc.title()
            description = metaOgDescription ?: descriptionAlt ?: ""
            imageUrl = metaOgImage ?: ""

            Log.d("METADATADEBUG", "Title: $title")
            Log.d("METADATADEBUG", "Image: $imageUrl")
            Log.d("METADATADEBUG", "Description: $description")
            return MetadataModel(
                title = title,
                description = description,
                thumbnailUrl = imageUrl,
            )
        } catch(e: Exception) {
            Log.d("METADATADEBUG", "Something went wrong: $e")
        }
        return MetadataModel.Empty()
    }

}