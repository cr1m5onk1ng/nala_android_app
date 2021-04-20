package com.example.nala.db.models.review.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nala.db.models.review.WordSenseDb
import com.example.nala.db.models.review.WordSenseDefinitionDb

data class WordSenseWithDefinitions (
    @Embedded val wordSense: WordSenseDb,
    @Relation(
        parentColumn="senseId",
        entityColumn="senseId"
    )
    val wordSenseDefinitions: List<WordSenseDefinitionDb>
)