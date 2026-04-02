package com.example.vocabmaster.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "words")
data class Word(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val russian: String,
    val english: String,
    var isMemorized: Boolean = false,
    var lastReviewed: Long = Date().time
)