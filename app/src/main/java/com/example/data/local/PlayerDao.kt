package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Player
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE teamId = :teamId ORDER BY jerseyNumber ASC")
    fun getPlayersForTeam(teamId: Long): Flow<List<Player>>

    @Query("SELECT * FROM players ORDER BY jerseyNumber ASC")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE id = :id LIMIT 1")
    fun getPlayerById(id: Long): Flow<Player?>

    @Query("SELECT * FROM players WHERE id = :id LIMIT 1")
    suspend fun getPlayerByIdDirect(id: Long): Player?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<Player>)

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)

    @Query("SELECT COUNT(*) FROM players WHERE teamId = :teamId")
    suspend fun getPlayersCountForTeam(teamId: Long): Int
}
