package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.CallUpStatus
import com.example.data.model.PaymentConcept
import com.example.data.model.PlayerPosition
import com.example.data.model.PlayerStatus
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole?): String = value?.name ?: UserRole.ADMIN.name

    @TypeConverter
    fun toUserRole(value: String?): UserRole =
        try { UserRole.valueOf(value ?: UserRole.ADMIN.name) } catch (_: Exception) { UserRole.ADMIN }

    @TypeConverter
    fun fromPlayerPosition(value: PlayerPosition?): String = value?.name ?: PlayerPosition.VOLANTE.name

    @TypeConverter
    fun toPlayerPosition(value: String?): PlayerPosition =
        try { PlayerPosition.valueOf(value ?: PlayerPosition.VOLANTE.name) } catch (_: Exception) { PlayerPosition.VOLANTE }

    @TypeConverter
    fun fromPlayerStatus(value: PlayerStatus?): String = value?.name ?: PlayerStatus.ACTIVO.name

    @TypeConverter
    fun toPlayerStatus(value: String?): PlayerStatus =
        try { PlayerStatus.valueOf(value ?: PlayerStatus.ACTIVO.name) } catch (_: Exception) { PlayerStatus.ACTIVO }

    @TypeConverter
    fun fromCallUpStatus(value: CallUpStatus?): String = value?.name ?: CallUpStatus.NO_CONFIRMADO.name

    @TypeConverter
    fun toCallUpStatus(value: String?): CallUpStatus =
        try { CallUpStatus.valueOf(value ?: CallUpStatus.NO_CONFIRMADO.name) } catch (_: Exception) { CallUpStatus.NO_CONFIRMADO }

    @TypeConverter
    fun fromPaymentConcept(value: PaymentConcept?): String = value?.name ?: PaymentConcept.ARBITRAJE.name

    @TypeConverter
    fun toPaymentConcept(value: String?): PaymentConcept =
        try { PaymentConcept.valueOf(value ?: PaymentConcept.ARBITRAJE.name) } catch (_: Exception) { PaymentConcept.ARBITRAJE }
}
