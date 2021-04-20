package com.example.nala.db.models.review.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nala.db.models.review.WordSenseDb
import com.example.nala.db.models.review.WordSenseTagDb

data class WordSenseWithTags (
    @Embedded val wordSense: WordSenseDb,
    @Relation(
        parentColumn="senseId",
        entityColumn="senseId"
    )
    val wordSenseTags: List<WordSenseTagDb>
)