package com.example.nala.utils.utilities

import com.example.nala.db.models.review.KanjiReviewCache
import com.example.nala.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import java.util.*
import javax.inject.Inject

class KanjiReviewsPager @Inject constructor(
    private val reviewRepository: ReviewRepository,
) : KeysetPager<Date?, KanjiReviewCache> {

    private var nextKey: Date? = null
    private var limit = 10

    override fun setKey(key: Date?) {
        nextKey = key
    }

    override fun setLimit(limit: Int) {
        this.limit = limit
    }

    override suspend fun getNextResult(): Flow<PagerResult<Date?, KanjiReviewCache>> {
        return reviewRepository.getKanjiReviewsPaged(this.nextKey, this.limit).mapLatest {
            val endReached = it.isEmpty()
            val key = if(it.isNotEmpty()) it.last().addedAt else null
            setKey(key)
            val result = PagerResult(
                data = it,
                nextKey = key,
                hasReachedEnd = endReached,
            )
            result
        }
    }
}