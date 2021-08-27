package com.example.nala.services.metadata

import android.util.Log
import ch.qos.logback.classic.spi.CallerData.extract
import com.example.nala.domain.model.metadata.MetadataModel
import it.skrape.core.htmlDocument
import it.skrape.fetcher.*
import it.skrape.selects.html5.meta
import it.skrape.selects.html5.title

class AsyncMetadataExtractorService : AsyncExtractorService<MetadataModel>  {
     override suspend fun extractFromUrl(documentUrl: String): MetadataModel {
        return skrape(AsyncFetcher) {
            request {
                url = documentUrl
                timeout = 20000
            }
            response {
                htmlDocument{
                    var title = titleText
                    if(title.isEmpty()) {
                        title = meta {
                            withAttribute = "property" to "og:title"
                            findFirst{
                                attribute("content")
                            }
                        }
                    }
                    var description = meta {
                        withAttribute = "property" to "og:description"
                        findFirst{
                            attribute("content")
                        }
                    }
                    if(description.isEmpty()) {
                        description = meta {
                            withAttribute = "property" to "description"
                            findFirst{
                                attribute("content")
                            }
                        }
                    }
                    val thumbnailUrl = meta {
                        withAttribute = "property" to "og:image"
                        findFirst{
                            attribute("content")
                        }
                    }
                    val model = MetadataModel(
                        title = title,
                        description = description,
                        thumbnailUrl = thumbnailUrl
                    )
                    Log.d("METADATADEBUG", "Extracted data: $model")
                    model
                }
            }
        }
    }
}