package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "matches",
    indices = [Index(value = ["teamId"])]
)
data class Match(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val teamId: Long = 1,
    val date: String,
    val time: String,
    val venue: String,
    val tournament: String = "Torneo de las Colonias",
    val opponent: String = "Rival",
    val refereeFeeTotal: Double = 80000.0,
    val refereeFeePerPlayer: Double = 10000.0,
    val isPlayed: Boolean = false,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val confirmationDeadline: String = "",
    val isFinalCallUpClosed: Boolean = false,
    val excludeUnresponsivePlayers: Boolean = true,
    val notes: String = ""
) {
    val displayTournament: String
        get() = tournament.ifBlank { "Torneo de las Colonias" }

    val displayOpponent: String
        get() = opponent.ifBlank { "Rival" }

    fun getConfirmationLink(baseUrl: String = "https://perlaverde.app"): String {
        return "$baseUrl/confirmar/$id"
    }
}
