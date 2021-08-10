package com.example.nala.domain.model.yt

import com.example.nala.domain.util.NetworkMapper
import com.example.nala.network.model.yt.comments.YoutubeVideoCommentsData

class YoutubeCommentMapper : NetworkMapper<YoutubeVideoCommentsData, YoutubeCommentsList> {

    override fun mapToDomainModel(networkModel: YoutubeVideoCommentsData): YoutubeCommentsList {
        val commentModels: List<YoutubeCommentModel> = networkModel.items?.map{
            YoutubeCommentModel(
                videoId = it?.snippet?.videoId ?: "",
                commentId = it?.id ?: "",
                content = it?.snippet?.topLevelComment?.snippet?.textOriginal ?: "",
                publishedAt = it?.snippet?.topLevelComment?.snippet?.publishedAt ?: "",
                authorName = it?.snippet?.topLevelComment?.snippet?.authorDisplayName ?: "",
                authorProfileImageUrl = it?.snippet?.topLevelComment?.snippet?.authorProfileImageUrl ?: "",
                likeCount = it?.snippet?.topLevelComment?.snippet?.likeCount ?: 0,
            )
        } ?: listOf()

        return YoutubeCommentsList(
            comments = commentModels,
            nextPageToken = networkModel.nextPageToken,
        )
    }

}