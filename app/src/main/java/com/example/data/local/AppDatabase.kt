package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CallUp
import com.example.data.model.CallUpStatus
import com.example.data.model.Match
import com.example.data.model.MatchStat
import com.example.data.model.Payment
import com.example.data.model.Player
import com.example.data.model.PlayerPosition
import com.example.data.model.PlayerStatus
import com.example.data.model.Substitution
import com.example.data.model.Team
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Team::class,
        Player::class,
        Match::class,
        CallUp::class,
        Payment::class,
        MatchStat::class,
        Substitution::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun callUpDao(): CallUpDao
    abstract fun paymentDao(): PaymentDao
    abstract fun matchStatDao(): MatchStatDao
    abstract fun substitutionDao(): SubstitutionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "perla_verde_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedInitialData(database)
                    }
                }
            }
        }

        private suspend fun seedInitialData(database: AppDatabase) {
            // Seed Team 1: Colonia San Luis - Perla Verde
            val teamId1 = database.teamDao().insertTeam(
                Team(
                    id = 1,
                    name = "Colonia San Luis – Perla Verde",
                    category = "Mayores / Libre",
                    primaryColorHex = "#1B5E20",
                    badgeIcon = "⚽",
                    notes = "Equipo principal de la Corporación Colombia Activa y Saludable, Medellín"
                )
            )

            // Seed Team 2: San Luis Veteranos
            database.teamDao().insertTeam(
                Team(
                    id = 2,
                    name = "San Luis Veteranos (+35)",
                    category = "Veteranos / Senior",
                    primaryColorHex = "#0D47A1",
                    badgeIcon = "🏆",
                    notes = "Categoría máster mayores de 35 años"
                )
            )

            // Seed sample players for Team 1 with cédula (documentNumber)
            val players = listOf(
                Player(teamId = teamId1, fullName = "Carlos Mario Restrepo", nickname = "El Loco", jerseyNumber = 1, position = PlayerPosition.ARQUERO, phoneNumber = "3001234567", documentNumber = "1020304001"),
                Player(teamId = teamId1, fullName = "Mateo Gómez Osorio", nickname = "Teo", jerseyNumber = 4, position = PlayerPosition.DEFENSA, phoneNumber = "3109876543", documentNumber = "1020304004"),
                Player(teamId = teamId1, fullName = "Juan David Zapata", nickname = "Zapata", jerseyNumber = 5, position = PlayerPosition.DEFENSA, phoneNumber = "3112345678", documentNumber = "1020304005"),
                Player(teamId = teamId1, fullName = "Santiago Álvarez Ruiz", nickname = "Santi", jerseyNumber = 3, position = PlayerPosition.DEFENSA, phoneNumber = "3158765432", documentNumber = "1020304003"),
                Player(teamId = teamId1, fullName = "Daniel Fernando Henao", nickname = "Dani", jerseyNumber = 8, position = PlayerPosition.VOLANTE, phoneNumber = "3013456789", documentNumber = "1020304008"),
                Player(teamId = teamId1, fullName = "Andrés Felipe Marín", nickname = "Pipe", jerseyNumber = 10, position = PlayerPosition.VOLANTE, phoneNumber = "3207654321", documentNumber = "1020304010"),
                Player(teamId = teamId1, fullName = "Sebastián Londoño", nickname = "Seba", jerseyNumber = 7, position = PlayerPosition.VOLANTE, phoneNumber = "3024567890", documentNumber = "1020304007"),
                Player(teamId = teamId1, fullName = "Alejandro Valencia", nickname = "Alejo", jerseyNumber = 11, position = PlayerPosition.DELANTERO, phoneNumber = "3146543210", documentNumber = "1020304011"),
                Player(teamId = teamId1, fullName = "Julián David Montoya", nickname = "El Tanque", jerseyNumber = 9, position = PlayerPosition.DELANTERO, phoneNumber = "3045678901", documentNumber = "1020304009"),
                Player(teamId = teamId1, fullName = "Kevin Alexander Blandón", nickname = "Blandón", jerseyNumber = 17, position = PlayerPosition.VOLANTE, phoneNumber = "3165432109", documentNumber = "1020304017"),
                Player(teamId = teamId1, fullName = "David Esteban Morales", nickname = "Morales", jerseyNumber = 2, position = PlayerPosition.DEFENSA, phoneNumber = "3056789012", documentNumber = "1020304002")
            )
            val playerIds = players.map { database.playerDao().insertPlayer(it) }

            // Seed an upcoming match for Team 1
            val matchId1 = database.matchDao().insertMatch(
                Match(
                    teamId = teamId1,
                    date = "2026-10-11",
                    time = "16:00",
                    venue = "Cancha Marte #1, Medellín",
                    tournament = "Torneo de las Colonias",
                    opponent = "Atlético San Juan",
                    refereeFeeTotal = 80000.0,
                    refereeFeePerPlayer = 10000.0,
                    confirmationDeadline = "2026-10-10 18:00",
                    isPlayed = false,
                    notes = "Llevar uniforme verde titular y canilleras obligatorias."
                )
            )

            // Seed initial call-ups for matchId1 with varied confirmation statuses
            val seedCallUps = listOf(
                CallUp(matchId = matchId1, playerId = playerIds.getOrElse(0) { 1L }, status = CallUpStatus.CONFIRMADO, confirmedAt = System.currentTimeMillis()),
                CallUp(matchId = matchId1, playerId = playerIds.getOrElse(1) { 2L }, status = CallUpStatus.CONFIRMADO, confirmedAt = System.currentTimeMillis()),
                CallUp(matchId = matchId1, playerId = playerIds.getOrElse(2) { 3L }, status = CallUpStatus.CONFIRMADO, confirmedAt = System.currentTimeMillis()),
                CallUp(matchId = matchId1, playerId = playerIds.getOrElse(3) { 4L }, status = CallUpStatus.CONFIRMADO, confirmedAt = System.currentTimeMillis()),
                CallUp(matchId = matchId1, playerId = playerIds.getOrElse(4) { 5L }, status = CallUpStatus.CONFIRMADO, confirmedAt = System.currentTimeMillis()),
                CallUp(matchId = matchId1, playerId = playerIds.getOrElse(5) { 6L }, status = CallUpStatus.CONFIRMADO, confirmedAt = System.currentTimeMillis()),
                CallUp(matchId = matchId1, playerId = playerIds.getOrElse(6) { 7L }, status = CallUpStatus.NO_CONFIRMADO),
                CallUp(matchId = matchId1, playerId = playerIds.getOrElse(7) { 8L }, status = CallUpStatus.NO_CONFIRMADO),
                CallUp(matchId = matchId1, playerId = playerIds.getOrElse(8) { 9L }, status = CallUpStatus.RECHAZADO, absenceReason = "Trabajo", confirmedAt = System.currentTimeMillis()),
                CallUp(matchId = matchId1, playerId = playerIds.getOrElse(9) { 10L }, status = CallUpStatus.NO_CONFIRMADO),
                CallUp(matchId = matchId1, playerId = playerIds.getOrElse(10) { 11L }, status = CallUpStatus.CONFIRMADO, confirmedAt = System.currentTimeMillis())
            )
            database.callUpDao().insertCallUps(seedCallUps)

            // Seed a played match with score for Team 1
            val matchId2 = database.matchDao().insertMatch(
                Match(
                    teamId = teamId1,
                    date = "2026-09-27",
                    time = "14:00",
                    venue = "Cancha Belén #2, Medellín",
                    tournament = "Torneo de las Colonias",
                    opponent = "Deportivo Belén F.C.",
                    refereeFeeTotal = 80000.0,
                    refereeFeePerPlayer = 10000.0,
                    isPlayed = true,
                    goalsFor = 3,
                    goalsAgainst = 1,
                    isFinalCallUpClosed = true,
                    notes = "Gran victoria de visita con doblete de Montoya y gol de Alejo."
                )
            )

            // Seed match stats for the played match
            database.matchStatDao().insertMatchStats(
                listOf(
                    MatchStat(matchId = matchId2, playerId = playerIds.getOrElse(8) { 9L }, goals = 2, assists = 0),
                    MatchStat(matchId = matchId2, playerId = playerIds.getOrElse(7) { 8L }, goals = 1, assists = 1),
                    MatchStat(matchId = matchId2, playerId = playerIds.getOrElse(5) { 6L }, goals = 0, assists = 1, yellowCards = 1)
                )
            )
        }
    }
}
