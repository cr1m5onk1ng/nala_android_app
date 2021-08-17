package com.example.nala.domain.model.yt

import com.example.nala.domain.util.NetworkMapper
import com.example.nala.network.model.yt.videos.YoutubeVideoDto

class YoutubeVideoMapper : NetworkMapper<YoutubeVideoDto, YoutubeVideoModel> {

    override fun mapToDomainModel(networkModel: YoutubeVideoDto): YoutubeVideoModel {
        return YoutubeVideoModel(
            id = networkModel.items?.first()?.id ?: "",
            url = "",
            publishedAt = networkModel.items?.first()?.snippet?.publishedAt ?: "",
            title = networkModel.items?.first()?.snippet?.title ?: "",
            thumbnailUrl = networkModel.items?.first()?.snippet?.thumbnails?.default?.url ?: "",
            channelTitle = networkModel.items?.first()?.snippet?.channelTitle ?: "",
            tags = networkModel.items?.first()?.snippet?.tags ?: listOf()
        )
    }
}