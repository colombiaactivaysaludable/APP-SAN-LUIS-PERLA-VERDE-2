package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    indices = [
        Index(value = ["teamId"]),
        Index(value = ["playerId"]),
        Index(value = ["matchId"])
    ]
)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val teamId: Long = 1,
    val playerId: Long,
    val concept: PaymentConcept = PaymentConcept.ARBITRAJE,
    val amountOwed: Double = 0.0,
    val amountPaid: Double = 0.0,
    val date: String,
    val matchId: Long? = null,
    val notes: String = ""
) {
    val pendingBalance: Double
        get() = (amountOwed - amountPaid).coerceAtLeast(0.0)

    val isSettled: Boolean
        get() = amountPaid >= amountOwed
}
