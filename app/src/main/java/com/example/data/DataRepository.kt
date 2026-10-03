package com.example.data

import com.example.data.local.AppDatabase
import com.example.data.model.CallUp
import com.example.data.model.CallUpStatus
import com.example.data.model.Match
import com.example.data.model.MatchStat
import com.example.data.model.Payment
import com.example.data.model.Player
import com.example.data.model.Substitution
import com.example.data.model.Team
import kotlinx.coroutines.flow.Flow

class DataRepository(private val database: AppDatabase) {

    // Teams
    val allTeams: Flow<List<Team>> = database.teamDao().getAllTeams()

    suspend fun saveTeam(team: Team): Long = database.teamDao().insertTeam(team)
    suspend fun deleteTeam(team: Team) = database.teamDao().deleteTeam(team)

    // Players
    fun getPlayersForTeam(teamId: Long): Flow<List<Player>> = database.playerDao().getPlayersForTeam(teamId)
    val allPlayers: Flow<List<Player>> = database.playerDao().getAllPlayers()

    suspend fun savePlayer(player: Player): Long = database.playerDao().insertPlayer(player)
    suspend fun deletePlayer(player: Player) = database.playerDao().deletePlayer(player)

    // Matches
    fun getMatchesForTeam(teamId: Long): Flow<List<Match>> = database.matchDao().getMatchesForTeam(teamId)
    val allMatches: Flow<List<Match>> = database.matchDao().getAllMatches()

    suspend fun saveMatch(match: Match): Long = database.matchDao().insertMatch(match)
    suspend fun deleteMatch(match: Match) = database.matchDao().deleteMatch(match)

    // CallUps
    fun getCallUpsForMatch(matchId: Long): Flow<List<CallUp>> = database.callUpDao().getCallUpsForMatch(matchId)
    val allCallUps: Flow<List<CallUp>> = database.callUpDao().getAllCallUps()

    suspend fun saveCallUp(callUp: CallUp): Long = database.callUpDao().insertCallUp(callUp)
    suspend fun saveCallUps(callUps: List<CallUp>) = database.callUpDao().insertCallUps(callUps)
    suspend fun deleteCallUp(callUp: CallUp) = database.callUpDao().deleteCallUp(callUp)
    suspend fun deleteCallUpsForMatch(matchId: Long) = database.callUpDao().deleteCallUpsForMatch(matchId)

    suspend fun confirmPlayerAttendance(
        matchId: Long,
        playerId: Long,
        confirmed: Boolean,
        absenceReason: String? = null
    ) {
        val existing = database.callUpDao().getCallUp(matchId, playerId)
        val status = if (confirmed) CallUpStatus.CONFIRMADO else CallUpStatus.RECHAZADO
        val reason = if (!confirmed) (absenceReason ?: "Personal") else null
        val now = System.currentTimeMillis()

        if (existing != null) {
            database.callUpDao().updateAttendance(matchId, playerId, status, reason, now)
        } else {
            database.callUpDao().insertCallUp(
                CallUp(
                    matchId = matchId,
                    playerId = playerId,
                    status = status,
                    absenceReason = reason,
                    confirmedAt = now
                )
            )
        }
    }

    // Payments
    fun getPaymentsForTeam(teamId: Long): Flow<List<Payment>> = database.paymentDao().getPaymentsForTeam(teamId)
    val allPayments: Flow<List<Payment>> = database.paymentDao().getAllPayments()

    suspend fun savePayment(payment: Payment): Long = database.paymentDao().insertPayment(payment)
    suspend fun deletePayment(payment: Payment) = database.paymentDao().deletePayment(payment)

    // Stats
    fun getStatsForMatch(matchId: Long): Flow<List<MatchStat>> = database.matchStatDao().getStatsForMatch(matchId)
    val allStats: Flow<List<MatchStat>> = database.matchStatDao().getAllStats()

    suspend fun saveMatchStat(stat: MatchStat): Long = database.matchStatDao().insertMatchStat(stat)
    suspend fun deleteMatchStat(stat: MatchStat) = database.matchStatDao().deleteMatchStat(stat)

    // Substitutions
    fun getSubsForMatch(matchId: Long): Flow<List<Substitution>> = database.substitutionDao().getSubsForMatch(matchId)
    val allSubs: Flow<List<Substitution>> = database.substitutionDao().getAllSubs()

    suspend fun saveSub(sub: Substitution): Long = database.substitutionDao().insertSub(sub)
    suspend fun deleteSub(sub: Substitution) = database.substitutionDao().deleteSub(sub)
}
