package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "match_stats",
    indices = [
        Index(value = ["matchId"]),
        Index(value = ["playerId"])
    ]
)
data class MatchStat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matchId: Long,
    val playerId: Long,
    val goals: Int = 0,
    val assists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val minutesPlayed: Int = 90
)
