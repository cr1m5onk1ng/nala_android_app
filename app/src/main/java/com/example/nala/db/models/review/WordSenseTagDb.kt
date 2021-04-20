package com.example.nala.db.models.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName="word_sense_tag",
    primaryKeys=["senseId", "tag"],
    /*
    foreignKeys=[
        ForeignKey(
            entity=WordSenseDb::class,
            parentColumns=arrayOf("senseId"),
            childColumns=arrayOf("senseId"),
            onDelete= ForeignKey.CASCADE
        )
    ]*/
)
data class WordSenseTagDb (

    @ColumnInfo(name="senseId")
    val senseId: String,

    @ColumnInfo(name="tag")
    val tag: String,
)