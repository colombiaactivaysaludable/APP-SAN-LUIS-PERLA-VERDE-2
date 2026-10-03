package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MatchStat
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchStatDao {
    @Query("SELECT * FROM match_stats WHERE matchId = :matchId")
    fun getStatsForMatch(matchId: Long): Flow<List<MatchStat>>

    @Query("SELECT * FROM match_stats WHERE playerId = :playerId")
    fun getStatsForPlayer(playerId: Long): Flow<List<MatchStat>>

    @Query("SELECT * FROM match_stats")
    fun getAllStats(): Flow<List<MatchStat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchStat(stat: MatchStat): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchStats(stats: List<MatchStat>)

    @Update
    suspend fun updateMatchStat(stat: MatchStat)

    @Delete
    suspend fun deleteMatchStat(stat: MatchStat)

    @Query("DELETE FROM match_stats WHERE matchId = :matchId")
    suspend fun deleteStatsForMatch(matchId: Long)
}
