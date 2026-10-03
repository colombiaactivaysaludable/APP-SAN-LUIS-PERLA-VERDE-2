package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "call_ups",
    indices = [
        Index(value = ["matchId"]),
        Index(value = ["playerId"]),
        Index(value = ["matchId", "playerId"], unique = true)
    ]
)
data class CallUp(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matchId: Long,
    val playerId: Long,
    val status: CallUpStatus = CallUpStatus.NO_CONFIRMADO,
    val absenceReason: String? = null,
    val paidRefereeFee: Boolean = false,
    val amountPaid: Double = 0.0,
    val confirmedAt: Long = 0L
)
