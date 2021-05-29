package com.example.nala.db.models.review.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nala.db.models.review.WordSenseDb
import com.example.nala.db.models.review.WordSenseDefinitionDb
import com.example.nala.db.models.review.WordSenseTagDb

data class WordSenseWithTagsAndDefinitions (
    @Embedded val sense: WordSenseDb,
    @Relation(
        entity=WordSenseTagDb::class,
        parentColumn="senseId",
        entityColumn="senseId"
    )
    val senseTags: List<WordSenseTagDb>?,

    @Relation(
        entity=WordSenseDefinitionDb::class,
        parentColumn="senseId",
        entityColumn="senseId"
    )
    val senseDefinitions: List<WordSenseDefinitionDb>?

    )
