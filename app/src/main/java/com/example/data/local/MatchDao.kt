package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches WHERE teamId = :teamId ORDER BY date DESC, time DESC")
    fun getMatchesForTeam(teamId: Long): Flow<List<Match>>

    @Query("SELECT * FROM matches ORDER BY date DESC, time DESC")
    fun getAllMatches(): Flow<List<Match>>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    fun getMatchById(id: Long): Flow<Match?>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    suspend fun getMatchByIdDirect(id: Long): Match?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<Match>)

    @Update
    suspend fun updateMatch(match: Match)

    @Delete
    suspend fun deleteMatch(match: Match)
}
