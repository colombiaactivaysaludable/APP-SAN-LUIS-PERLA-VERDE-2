package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CallUp
import com.example.data.model.CallUpStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CallUpDao {
    @Query("SELECT * FROM call_ups WHERE matchId = :matchId")
    fun getCallUpsForMatch(matchId: Long): Flow<List<CallUp>>

    @Query("SELECT * FROM call_ups WHERE matchId = :matchId")
    suspend fun getCallUpsForMatchDirect(matchId: Long): List<CallUp>

    @Query("SELECT * FROM call_ups")
    fun getAllCallUps(): Flow<List<CallUp>>

    @Query("SELECT * FROM call_ups WHERE matchId = :matchId AND playerId = :playerId LIMIT 1")
    suspend fun getCallUp(matchId: Long, playerId: Long): CallUp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallUp(callUp: CallUp): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallUps(callUps: List<CallUp>)

    @Update
    suspend fun updateCallUp(callUp: CallUp)

    @Delete
    suspend fun deleteCallUp(callUp: CallUp)

    @Query("DELETE FROM call_ups WHERE matchId = :matchId")
    suspend fun deleteCallUpsForMatch(matchId: Long)

    @Query("UPDATE call_ups SET status = :status, absenceReason = :absenceReason, confirmedAt = :confirmedAt WHERE matchId = :matchId AND playerId = :playerId")
    suspend fun updateAttendance(matchId: Long, playerId: Long, status: CallUpStatus, absenceReason: String?, confirmedAt: Long)
}
