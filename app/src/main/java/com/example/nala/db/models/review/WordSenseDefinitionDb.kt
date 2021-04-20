package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName="word_sense_definition",
    primaryKeys=["senseId", "definition"],
    /*
    foreignKeys=[
        ForeignKey(
            entity=WordSenseDb::class,
            parentColumns=arrayOf("senseId"),
            childColumns=arrayOf("senseId"),
            onDelete=ForeignKey.CASCADE
        )
    ]*/
)
data class WordSenseDefinitionDb (
    @ColumnInfo(name="senseId")
    val senseId: String,

    @ColumnInfo(name="definition")
    val definition: String,
)