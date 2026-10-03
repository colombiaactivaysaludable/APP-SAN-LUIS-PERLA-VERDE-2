package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class Team(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String = "Mayores / Libre",
    val primaryColorHex: String = "#1B5E20",
    val badgeIcon: String = "⚽",
    val notes: String = ""
)
