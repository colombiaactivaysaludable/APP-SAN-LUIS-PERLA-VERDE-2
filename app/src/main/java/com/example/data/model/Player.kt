package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "players",
    indices = [Index(value = ["teamId"])]
)
data class Player(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val teamId: Long = 1,
    val fullName: String,
    val nickname: String = "",
    val jerseyNumber: Int,
    val position: PlayerPosition = PlayerPosition.VOLANTE,
    val status: PlayerStatus = PlayerStatus.ACTIVO,
    val phoneNumber: String = "",
    val documentNumber: String = "", // Cédula de ciudadanía para verificación de 2 últimos dígitos
    val photoUri: String? = null,
    val notes: String = ""
) {
    val lastTwoDigitsOfId: String
        get() {
            val digits = documentNumber.filter { it.isDigit() }
            return when {
                digits.length >= 2 -> digits.takeLast(2)
                phoneNumber.filter { it.isDigit() }.length >= 2 -> phoneNumber.filter { it.isDigit() }.takeLast(2)
                else -> String.format("%02d", jerseyNumber)
            }
        }
}
