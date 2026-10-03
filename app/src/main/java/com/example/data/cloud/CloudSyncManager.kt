package com.example.data.cloud

import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.model.CallUp
import com.example.data.model.CallUpStatus
import com.example.data.model.Match
import com.example.data.model.MatchStat
import com.example.data.model.Payment
import com.example.data.model.PaymentConcept
import com.example.data.model.Player
import com.example.data.model.PlayerPosition
import com.example.data.model.PlayerStatus
import com.example.data.model.Substitution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CloudSyncManager(private val database: AppDatabase, private val context: Context) {

    suspend fun exportTeamDataJson(teamId: Long = 1L): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("version", 1)
        root.put("teamId", teamId)
        root.put("teamName", "Colonia San Luis – Perla Verde")
        root.put("organization", "Corporación Colombia Activa y Saludable")
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))

        // Players
        val players = database.playerDao().getAllPlayers().first()
        val playersArray = JSONArray()
        for (p in players) {
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("teamId", p.teamId)
            obj.put("fullName", p.fullName)
            obj.put("nickname", p.nickname)
            obj.put("jerseyNumber", p.jerseyNumber)
            obj.put("position", p.position.name)
            obj.put("status", p.status.name)
            obj.put("phoneNumber", p.phoneNumber)
            obj.put("documentNumber", p.documentNumber)
            obj.put("photoUri", p.photoUri ?: "")
            obj.put("notes", p.notes)
            playersArray.put(obj)
        }
        root.put("players", playersArray)

        // Matches
        val matches = database.matchDao().getAllMatches().first()
        val matchesArray = JSONArray()
        for (m in matches) {
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("teamId", m.teamId)
            obj.put("date", m.date)
            obj.put("time", m.time)
            obj.put("venue", m.venue)
            obj.put("tournament", m.tournament)
            obj.put("opponent", m.opponent)
            obj.put("refereeFeeTotal", m.refereeFeeTotal)
            obj.put("refereeFeePerPlayer", m.refereeFeePerPlayer)
            obj.put("isPlayed", m.isPlayed)
            obj.put("goalsFor", m.goalsFor)
            obj.put("goalsAgainst", m.goalsAgainst)
            obj.put("confirmationDeadline", m.confirmationDeadline)
            obj.put("isFinalCallUpClosed", m.isFinalCallUpClosed)
            obj.put("excludeUnresponsivePlayers", m.excludeUnresponsivePlayers)
            obj.put("notes", m.notes)
            matchesArray.put(obj)
        }
        root.put("matches", matchesArray)

        // CallUps
        val callUps = database.callUpDao().getAllCallUps().first()
        val callUpsArray = JSONArray()
        for (c in callUps) {
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("matchId", c.matchId)
            obj.put("playerId", c.playerId)
            obj.put("status", c.status.name)
            obj.put("absenceReason", c.absenceReason ?: "")
            obj.put("paidRefereeFee", c.paidRefereeFee)
            obj.put("amountPaid", c.amountPaid)
            obj.put("confirmedAt", c.confirmedAt)
            callUpsArray.put(obj)
        }
        root.put("callUps", callUpsArray)

        // MatchStats
        val stats = database.matchStatDao().getAllStats().first()
        val statsArray = JSONArray()
        for (s in stats) {
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("matchId", s.matchId)
            obj.put("playerId", s.playerId)
            obj.put("goals", s.goals)
            obj.put("assists", s.assists)
            obj.put("yellowCards", s.yellowCards)
            obj.put("redCards", s.redCards)
            obj.put("minutesPlayed", s.minutesPlayed)
            statsArray.put(obj)
        }
        root.put("matchStats", statsArray)

        // Substitutions
        val subs = database.substitutionDao().getAllSubs().first()
        val subsArray = JSONArray()
        for (sub in subs) {
            val obj = JSONObject()
            obj.put("id", sub.id)
            obj.put("matchId", sub.matchId)
            obj.put("playerOutId", sub.playerOutId)
            obj.put("playerInId", sub.playerInId)
            obj.put("minute", sub.minute)
            subsArray.put(obj)
        }
        root.put("substitutions", subsArray)

        // Payments
        val payments = database.paymentDao().getAllPayments().first()
        val paymentsArray = JSONArray()
        for (pay in payments) {
            val obj = JSONObject()
            obj.put("id", pay.id)
            obj.put("teamId", pay.teamId)
            obj.put("playerId", pay.playerId)
            obj.put("concept", pay.concept.name)
            obj.put("amountOwed", pay.amountOwed)
            obj.put("amountPaid", pay.amountPaid)
            obj.put("date", pay.date)
            obj.put("matchId", pay.matchId ?: -1L)
            obj.put("notes", pay.notes)
            paymentsArray.put(obj)
        }
        root.put("payments", paymentsArray)

        root.toString(2)
    }

    suspend fun importTeamDataJson(jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            val teamId = root.optLong("teamId", 1L)

            // Parse Players
            val playersArray = root.optJSONArray("players") ?: JSONArray()
            val players = mutableListOf<Player>()
            for (i in 0 until playersArray.length()) {
                val obj = playersArray.getJSONObject(i)
                players.add(
                    Player(
                        id = obj.optLong("id", 0),
                        teamId = obj.optLong("teamId", teamId),
                        fullName = obj.getString("fullName"),
                        nickname = obj.optString("nickname", ""),
                        jerseyNumber = obj.optInt("jerseyNumber", 0),
                        position = try { PlayerPosition.valueOf(obj.optString("position", "VOLANTE")) } catch (_: Exception) { PlayerPosition.VOLANTE },
                        status = try { PlayerStatus.valueOf(obj.optString("status", "ACTIVO")) } catch (_: Exception) { PlayerStatus.ACTIVO },
                        phoneNumber = obj.optString("phoneNumber", ""),
                        documentNumber = obj.optString("documentNumber", ""),
                        photoUri = obj.optString("photoUri").takeIf { it.isNotBlank() },
                        notes = obj.optString("notes", "")
                    )
                )
            }

            // Parse Matches
            val matchesArray = root.optJSONArray("matches") ?: JSONArray()
            val matches = mutableListOf<Match>()
            for (i in 0 until matchesArray.length()) {
                val obj = matchesArray.getJSONObject(i)
                matches.add(
                    Match(
                        id = obj.optLong("id", 0),
                        teamId = obj.optLong("teamId", teamId),
                        date = obj.optString("date", ""),
                        time = obj.optString("time", ""),
                        venue = obj.optString("venue", ""),
                        tournament = obj.optString("tournament", "Torneo de las Colonias"),
                        opponent = obj.optString("opponent", "Rival"),
                        refereeFeeTotal = obj.optDouble("refereeFeeTotal", 80000.0),
                        refereeFeePerPlayer = obj.optDouble("refereeFeePerPlayer", 10000.0),
                        isPlayed = obj.optBoolean("isPlayed", false),
                        goalsFor = obj.optInt("goalsFor", 0),
                        goalsAgainst = obj.optInt("goalsAgainst", 0),
                        confirmationDeadline = obj.optString("confirmationDeadline", ""),
                        isFinalCallUpClosed = obj.optBoolean("isFinalCallUpClosed", false),
                        excludeUnresponsivePlayers = obj.optBoolean("excludeUnresponsivePlayers", true),
                        notes = obj.optString("notes", "")
                    )
                )
            }

            // Parse CallUps
            val callUpsArray = root.optJSONArray("callUps") ?: JSONArray()
            val callUps = mutableListOf<CallUp>()
            for (i in 0 until callUpsArray.length()) {
                val obj = callUpsArray.getJSONObject(i)
                callUps.add(
                    CallUp(
                        id = obj.optLong("id", 0),
                        matchId = obj.getLong("matchId"),
                        playerId = obj.getLong("playerId"),
                        status = try { CallUpStatus.valueOf(obj.optString("status", "NO_CONFIRMADO")) } catch (_: Exception) { CallUpStatus.NO_CONFIRMADO },
                        absenceReason = obj.optString("absenceReason").takeIf { it.isNotBlank() },
                        paidRefereeFee = obj.optBoolean("paidRefereeFee", false),
                        amountPaid = obj.optDouble("amountPaid", 0.0),
                        confirmedAt = obj.optLong("confirmedAt", 0L)
                    )
                )
            }

            // Parse MatchStats
            val statsArray = root.optJSONArray("matchStats") ?: JSONArray()
            val stats = mutableListOf<MatchStat>()
            for (i in 0 until statsArray.length()) {
                val obj = statsArray.getJSONObject(i)
                stats.add(
                    MatchStat(
                        id = obj.optLong("id", 0),
                        matchId = obj.getLong("matchId"),
                        playerId = obj.getLong("playerId"),
                        goals = obj.optInt("goals", 0),
                        assists = obj.optInt("assists", 0),
                        yellowCards = obj.optInt("yellowCards", 0),
                        redCards = obj.optInt("redCards", 0),
                        minutesPlayed = obj.optInt("minutesPlayed", 90)
                    )
                )
            }

            // Parse Subs
            val subsArray = root.optJSONArray("substitutions") ?: JSONArray()
            val subs = mutableListOf<Substitution>()
            for (i in 0 until subsArray.length()) {
                val obj = subsArray.getJSONObject(i)
                subs.add(
                    Substitution(
                        id = obj.optLong("id", 0),
                        matchId = obj.getLong("matchId"),
                        playerOutId = obj.getLong("playerOutId"),
                        playerInId = obj.getLong("playerInId"),
                        minute = obj.optInt("minute", 0)
                    )
                )
            }

            // Parse Payments
            val paymentsArray = root.optJSONArray("payments") ?: JSONArray()
            val payments = mutableListOf<Payment>()
            for (i in 0 until paymentsArray.length()) {
                val obj = paymentsArray.getJSONObject(i)
                val mId = obj.optLong("matchId", -1L)
                payments.add(
                    Payment(
                        id = obj.optLong("id", 0),
                        teamId = obj.optLong("teamId", teamId),
                        playerId = obj.getLong("playerId"),
                        concept = try { PaymentConcept.valueOf(obj.optString("concept", "ARBITRAJE")) } catch (_: Exception) { PaymentConcept.ARBITRAJE },
                        amountOwed = obj.optDouble("amountOwed", 0.0),
                        amountPaid = obj.optDouble("amountPaid", 0.0),
                        date = obj.optString("date", ""),
                        matchId = if (mId > 0) mId else null,
                        notes = obj.optString("notes", "")
                    )
                )
            }

            // Save to database
            if (players.isNotEmpty()) database.playerDao().insertPlayers(players)
            if (matches.isNotEmpty()) database.matchDao().insertMatches(matches)
            if (callUps.isNotEmpty()) database.callUpDao().insertCallUps(callUps)
            if (stats.isNotEmpty()) database.matchStatDao().insertMatchStats(stats)
            if (subs.isNotEmpty()) database.substitutionDao().insertSubs(subs)
            if (payments.isNotEmpty()) database.paymentDao().insertPayments(payments)

            val totalImported = players.size + matches.size + payments.size
            Result.success(totalImported)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun shareDataBackupIntent(jsonContent: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, jsonContent)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartir Copia de Seguridad JSON")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}
