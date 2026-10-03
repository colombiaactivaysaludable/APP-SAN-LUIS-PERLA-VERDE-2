package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Substitution
import kotlinx.coroutines.flow.Flow

@Dao
interface SubstitutionDao {
    @Query("SELECT * FROM substitutions WHERE matchId = :matchId ORDER BY minute ASC")
    fun getSubsForMatch(matchId: Long): Flow<List<Substitution>>

    @Query("SELECT * FROM substitutions ORDER BY minute ASC")
    fun getAllSubs(): Flow<List<Substitution>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSub(substitution: Substitution): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubs(substitutions: List<Substitution>)

    @Update
    suspend fun updateSub(substitution: Substitution)

    @Delete
    suspend fun deleteSub(substitution: Substitution)

    @Query("DELETE FROM substitutions WHERE matchId = :matchId")
    suspend fun deleteSubsForMatch(matchId: Long)
}
