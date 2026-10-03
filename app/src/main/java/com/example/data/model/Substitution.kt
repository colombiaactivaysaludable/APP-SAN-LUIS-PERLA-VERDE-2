package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "substitutions",
    indices = [
        Index(value = ["matchId"]),
        Index(value = ["playerOutId"]),
        Index(value = ["playerInId"])
    ]
)
data class Substitution(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matchId: Long,
    val playerOutId: Long,
    val playerInId: Long,
    val minute: Int = 0
)
