package com.example.nala.domain.model.yt

import com.example.nala.domain.util.NetworkMapper
import com.example.nala.network.model.yt.threads.YoutubeVideoCommentsData

class YoutubeCommentMapper : NetworkMapper<YoutubeVideoCommentsData, YoutubeCommentsList> {

    override fun mapToDomainModel(networkModel: YoutubeVideoCommentsData): YoutubeCommentsList {
        val commentModels: List<YoutubeCommentModel> = networkModel.items?.map{ comment ->
            YoutubeCommentModel(
                videoId = comment.snippet?.videoId ?: "",
                commentId = comment.id ?: "",
                content = comment.snippet?.topLevelComment?.snippet?.textOriginal ?: "",
                publishedAt = comment.snippet?.topLevelComment?.snippet?.publishedAt ?: "",
                authorName = comment.snippet?.topLevelComment?.snippet?.authorDisplayName ?: "",
                authorProfileImageUrl = comment.snippet?.topLevelComment?.snippet?.authorProfileImageUrl ?: "",
                likeCount = comment.snippet?.topLevelComment?.snippet?.likeCount ?: 0,
                dislikesCount = 0,
                replies = comment.replies?.comments?.map{
                    YoutubeCommentModel(
                        videoId = it.snippet?.videoId ?: "",
                        commentId = it.id ?: "",
                        content = it.snippet?.textOriginal ?: "",
                        publishedAt = it.snippet?.publishedAt ?: "",
                        authorName = it.snippet?.authorDisplayName ?: "",
                        authorProfileImageUrl = it.snippet?.authorProfileImageUrl ?: "",
                        likeCount = it.snippet?.likeCount ?: 0,
                        dislikesCount = 0,
                    )
                } ?: listOf()
            )
        } ?: listOf()

        return YoutubeCommentsList(
            comments = commentModels,
            nextPageToken = networkModel.nextPageToken,
        )
    }

}