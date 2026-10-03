package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DataRepository
import com.example.data.cloud.CloudSyncManager
import com.example.data.local.AppDatabase
import com.example.data.model.CallUp
import com.example.data.model.CallUpStatus
import com.example.data.model.Match
import com.example.data.model.MatchStat
import com.example.data.model.Payment
import com.example.data.model.PaymentConcept
import com.example.data.model.Player
import com.example.data.model.PlayerStatus
import com.example.data.model.Substitution
import com.example.data.model.Team
import com.example.data.model.UserRole
import com.example.ui.components.formatCurrencyCop
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder

typealias PlayerFinancialSummary = DebtorSummary

data class DebtorSummary(
    val player: Player,
    val totalOwed: Double,
    val totalPaid: Double,
    val pendingBalance: Double
)

data class ScorerSummary(
    val player: Player,
    val totalGoals: Int,
    val totalAssists: Int,
    val matchesPlayed: Int
)

data class PlayerStatsSummary(
    val player: Player,
    val matchesPlayed: Int = 0,
    val totalGoals: Int = 0,
    val totalAssists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository: DataRepository = DataRepository(AppDatabase.getDatabase(application))

    // Navigation and Role State
    val currentTab = MutableStateFlow(0) // 0: Inicio, 1: Partidos, 2: Jugadores, 3: Pagos, 4: Goles/Stats
    val viewingPlayerDetail = MutableStateFlow<Player?>(null)

    private val _currentRole = MutableStateFlow(UserRole.ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole

    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    val selectedJugadorId = MutableStateFlow<Long?>(null)

    // Multi-Team Management
    val allTeams: StateFlow<List<Team>> = repository.allTeams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTeamId = MutableStateFlow(1L)

    val activeTeam: StateFlow<Team?> = combine(allTeams, activeTeamId) { teams, id ->
        teams.find { it.id == id } ?: teams.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setActiveTeam(team: Team) {
        activeTeamId.value = team.id
    }

    // Dynamic Team Scoped Flows
    val allPlayers: StateFlow<List<Player>> = activeTeamId.flatMapLatest { teamId ->
        repository.getPlayersForTeam(teamId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedPlayerForJugadorView: StateFlow<Player?> = combine(allPlayers, selectedJugadorId) { players, id ->
        if (id != null) players.find { it.id == id } else players.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setSelectedPlayerForJugador(player: Player) {
        selectedJugadorId.value = player.id
    }

    val syncManager: CloudSyncManager = CloudSyncManager(AppDatabase.getDatabase(application), application)

    val allMatches: StateFlow<List<Match>> = activeTeamId.flatMapLatest { teamId ->
        repository.getMatchesForTeam(teamId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<Payment>> = activeTeamId.flatMapLatest { teamId ->
        repository.getPaymentsForTeam(teamId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCallUps: StateFlow<List<CallUp>> = repository.allCallUps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStats: StateFlow<List<MatchStat>> = repository.allStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubs: StateFlow<List<Substitution>> = repository.allSubs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial Summaries
    val debtorsList: StateFlow<List<DebtorSummary>> = combine(allPlayers, allPayments, allCallUps, allMatches) { players, payments, callUps, matches ->
        players.map { player ->
            val playerPayments = payments.filter { it.playerId == player.id }
            val directOwed = playerPayments.sumOf { it.amountOwed }
            val directPaid = playerPayments.sumOf { it.amountPaid }

            val playerCallUps = callUps.filter { it.playerId == player.id }
            var matchOwed = 0.0
            var matchPaid = 0.0

            for (cu in playerCallUps) {
                val match = matches.find { it.id == cu.matchId }
                if (match != null) {
                    val hasExistingPayment = playerPayments.any { it.matchId == match.id && it.concept == PaymentConcept.ARBITRAJE }
                    if (!hasExistingPayment) {
                        matchOwed += match.refereeFeePerPlayer
                        if (cu.paidRefereeFee) {
                            matchPaid += match.refereeFeePerPlayer
                        }
                    }
                }
            }

            val totalOwed = directOwed + matchOwed
            val totalPaid = directPaid + matchPaid
            val pending = (totalOwed - totalPaid).coerceAtLeast(0.0)

            DebtorSummary(
                player = player,
                totalOwed = totalOwed,
                totalPaid = totalPaid,
                pendingBalance = pending
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPendingDebt: StateFlow<Double> = debtorsList.combine(debtorsList) { list, _ ->
        list.sumOf { it.pendingBalance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCollected: StateFlow<Double> = debtorsList.combine(debtorsList) { list, _ ->
        list.sumOf { it.totalPaid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Top Scorers
    val topScorers: StateFlow<List<ScorerSummary>> = combine(allPlayers, allStats) { players, stats ->
        players.map { player ->
            val playerStats = stats.filter { it.playerId == player.id }
            val goals = playerStats.sumOf { it.goals }
            val assists = playerStats.sumOf { it.assists }
            val matches = playerStats.map { it.matchId }.distinct().size
            ScorerSummary(player, goals, assists, matches)
        }.filter { it.totalGoals > 0 || it.totalAssists > 0 }
            .sortedWith(compareByDescending<ScorerSummary> { it.totalGoals }.thenByDescending { it.totalAssists })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playerStatsSummaries: StateFlow<List<PlayerStatsSummary>> = combine(allPlayers, allStats) { players, stats ->
        players.map { player ->
            val playerStats = stats.filter { it.playerId == player.id }
            PlayerStatsSummary(
                player = player,
                matchesPlayed = playerStats.map { it.matchId }.distinct().size,
                totalGoals = playerStats.sumOf { it.goals },
                totalAssists = playerStats.sumOf { it.assists },
                yellowCards = playerStats.sumOf { it.yellowCards },
                redCards = playerStats.sumOf { it.redCards }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Player Actions
    fun savePlayer(player: Player, onDone: () -> Unit = {}) {
        if (_currentRole.value != UserRole.ADMIN) return
        viewModelScope.launch {
            val playerWithTeam = if (player.teamId <= 0L) player.copy(teamId = activeTeamId.value) else player
            repository.savePlayer(playerWithTeam)
            onDone()
        }
    }

    fun deletePlayer(player: Player, onDone: () -> Unit = {}) {
        if (_currentRole.value != UserRole.ADMIN) return
        viewModelScope.launch {
            repository.deletePlayer(player)
            onDone()
        }
    }

    // Match Actions
    fun saveMatch(match: Match, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val matchWithTeam = if (match.teamId <= 0L) match.copy(teamId = activeTeamId.value) else match
            repository.saveMatch(matchWithTeam)
            onDone()
        }
    }

    fun deleteMatch(match: Match, onDone: () -> Unit = {}) {
        if (_currentRole.value != UserRole.ADMIN) return
        viewModelScope.launch {
            repository.deleteMatch(match)
            onDone()
        }
    }

    fun updateMatchScore(
        matchId: Long,
        goalsFor: Int,
        goalsAgainst: Int,
        isPlayed: Boolean = true,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val current = allMatches.value.find { it.id == matchId } ?: return@launch
            val updated = current.copy(
                goalsFor = goalsFor,
                goalsAgainst = goalsAgainst,
                isPlayed = isPlayed
            )
            repository.saveMatch(updated)
            onDone()
        }
    }

    // CallUp Actions
    fun toggleCallUpRefereePaid(callUp: CallUp, match: Match) {
        viewModelScope.launch {
            val newPaid = !callUp.paidRefereeFee
            val newAmount = if (newPaid) match.refereeFeePerPlayer else 0.0
            repository.saveCallUp(callUp.copy(paidRefereeFee = newPaid, amountPaid = newAmount))
        }
    }

    fun updateCallUpStatus(callUp: CallUp, status: CallUpStatus, reason: String? = null) {
        viewModelScope.launch {
            repository.saveCallUp(callUp.copy(status = status, absenceReason = reason, confirmedAt = System.currentTimeMillis()))
        }
    }

    fun removePlayerFromMatch(callUp: CallUp) {
        viewModelScope.launch {
            repository.deleteCallUp(callUp)
        }
    }

    fun addPlayersToMatch(matchId: Long, playerIds: List<Long>, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val current = repository.getCallUpsForMatch(matchId)
            val callUps = playerIds.map { pId ->
                CallUp(matchId = matchId, playerId = pId, status = CallUpStatus.NO_CONFIRMADO)
            }
            repository.saveCallUps(callUps)
            onDone()
        }
    }

    fun convokeAllActivePlayers(matchId: Long, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val active = allPlayers.value.filter { it.status != PlayerStatus.INACTIVO }
            val existing = allCallUps.value.filter { it.matchId == matchId }.map { it.playerId }.toSet()
            val toAdd = active.filter { it.id !in existing }.map { p ->
                CallUp(matchId = matchId, playerId = p.id, status = CallUpStatus.NO_CONFIRMADO)
            }
            if (toAdd.isNotEmpty()) {
                repository.saveCallUps(toAdd)
            }
            onDone()
        }
    }

    fun removeAllCallUpsForMatch(matchId: Long, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteCallUpsForMatch(matchId)
            onDone()
        }
    }

    // Payment Actions
    fun savePayment(payment: Payment, onDone: () -> Unit = {}) {
        if (_currentRole.value != UserRole.ADMIN) return
        viewModelScope.launch {
            val paymentWithTeam = if (payment.teamId <= 0L) payment.copy(teamId = activeTeamId.value) else payment
            repository.savePayment(paymentWithTeam)
            onDone()
        }
    }

    fun deletePayment(payment: Payment, onDone: () -> Unit = {}) {
        if (_currentRole.value != UserRole.ADMIN) return
        viewModelScope.launch {
            repository.deletePayment(payment)
            onDone()
        }
    }

    // Stats and Substitutions
    fun saveMatchStat(stat: MatchStat, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveMatchStat(stat)
            onDone()
        }
    }

    fun deleteMatchStat(stat: MatchStat, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteMatchStat(stat)
            onDone()
        }
    }

    fun saveSub(sub: Substitution, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveSub(sub)
            onDone()
        }
    }

    fun deleteSub(sub: Substitution, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteSub(sub)
            onDone()
        }
    }

    // WhatsApp Helpers
    fun openIndividualWhatsApp(context: Context, player: Player, match: Match) {
        val message = """
🟢⚪ COLONIA SAN LUIS – PERLA VERDE ⚪🟢

Hola ${player.fullName}, estás convocado para el partido:
📅 Fecha: ${match.date}
⏰ Hora: ${match.time}
📍 Cancha: ${match.venue}
🏆 Torneo: ${match.displayTournament} vs ${match.displayOpponent}
💵 Arbitraje: ${formatCurrencyCop(match.refereeFeePerPlayer)}

👉 Confirma tu asistencia aquí:
${match.getConfirmationLink()}

¡Vamos con toda! 💪
        """.trimIndent()

        val phone = player.phoneNumber.filter { it.isDigit() }
        val uri = if (phone.isNotBlank()) {
            val formatted = if (phone.startsWith("57")) phone else "57$phone"
            Uri.parse("https://api.whatsapp.com/send?phone=$formatted&text=${URLEncoder.encode(message, "UTF-8")}")
        } else {
            Uri.parse("https://api.whatsapp.com/send?text=${URLEncoder.encode(message, "UTF-8")}")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun confirmPlayerAttendance(
        matchId: Long,
        playerId: Long,
        confirmed: Boolean,
        absenceReason: String? = null,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.confirmPlayerAttendance(matchId, playerId, confirmed, absenceReason)
            onDone()
        }
    }

    fun closeFinalCallUp(
        match: Match,
        excludeUnresponsive: Boolean = true,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val updated = match.copy(
                isFinalCallUpClosed = true,
                excludeUnresponsivePlayers = excludeUnresponsive
            )
            repository.saveMatch(updated)
            onDone()
        }
    }
}
