package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CallUp
import com.example.data.model.CallUpStatus
import com.example.data.model.Match
import com.example.data.model.MatchStat
import com.example.data.model.Player
import com.example.data.model.Substitution
import com.example.ui.MainViewModel
import com.example.ui.theme.CardRed
import com.example.ui.theme.CardYellow
import com.example.ui.theme.PerlaGreenPrimary

@Composable
fun MatchStatsDialog(
    match: Match,
    callUps: List<CallUp>,
    allPlayers: List<Player>,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val allStats by viewModel.allStats.collectAsStateWithLifecycle()
    val allSubs by viewModel.allSubs.collectAsStateWithLifecycle()

    val matchStats = allStats.filter { it.matchId == match.id }
    val matchSubs = allSubs.filter { it.matchId == match.id }

    var goalsForText by remember { mutableStateOf(match.goalsFor.toString()) }
    var goalsAgainstText by remember { mutableStateOf(match.goalsAgainst.toString()) }

    // Dialog state for adding a stat
    var showAddStatDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Planilla y Marcador Oficial", fontWeight = FontWeight.Bold, color = PerlaGreenPrimary, fontSize = 17.sp)
                Text("vs ${match.displayOpponent} • ${match.date}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Score Board
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("MARCADOR FINAL", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PerlaGreenPrimary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Perla Verde", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = goalsForText,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) goalsForText = it },
                                    singleLine = true,
                                    modifier = Modifier.width(60.dp)
                                )
                            }

                            Text(" - ", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(horizontal = 14.dp))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(match.displayOpponent, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = goalsAgainstText,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) goalsAgainstText = it },
                                    singleLine = true,
                                    modifier = Modifier.width(60.dp)
                                )
                            }
                        }
                    }
                }

                // Goles y Sanciones Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Goles y Amonestaciones", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Button(
                        onClick = { showAddStatDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Registro", fontSize = 12.sp)
                    }
                }

                if (matchStats.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEEEEEE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No hay goles ni tarjetas registradas para este partido.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        matchStats.forEach { stat ->
                            val player = allPlayers.find { it.id == stat.playerId }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(shape = CircleShape, color = PerlaGreenPrimary, modifier = Modifier.size(30.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("#${player?.jerseyNumber ?: 0}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(player?.fullName ?: "Jugador", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            if (stat.goals > 0) Text("⚽ ${stat.goals} gol(es)", fontSize = 11.sp, color = PerlaGreenPrimary)
                                            if (stat.assists > 0) Text("👟 ${stat.assists} asist.", fontSize = 11.sp, color = Color.DarkGray)
                                            if (stat.yellowCards > 0) Text("🟨 ${stat.yellowCards}", fontSize = 11.sp)
                                            if (stat.redCards > 0) Text("🟥 ${stat.redCards}", fontSize = 11.sp)
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteMatchStat(stat) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val gf = goalsForText.toIntOrNull() ?: match.goalsFor
                    val ga = goalsAgainstText.toIntOrNull() ?: match.goalsAgainst
                    viewModel.updateMatchScore(
                        matchId = match.id,
                        goalsFor = gf,
                        goalsAgainst = ga,
                        isPlayed = true
                    ) {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary)
            ) {
                Text("Guardar Marcador")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )

    if (showAddStatDialog) {
        val activeCallUpPlayers = callUps.mapNotNull { cu -> allPlayers.find { it.id == cu.playerId } }
            .ifEmpty { allPlayers }

        var selectedPlayer by remember { mutableStateOf(activeCallUpPlayers.firstOrNull()) }
        var goals by remember { mutableStateOf(1) }
        var assists by remember { mutableStateOf(0) }
        var yellowCards by remember { mutableStateOf(0) }
        var redCards by remember { mutableStateOf(0) }

        AlertDialog(
            onDismissRequest = { showAddStatDialog = false },
            title = { Text("Registrar Incidencia de Partido") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Jugador:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    LazyColumn(modifier = Modifier.height(140.dp)) {
                        items(activeCallUpPlayers) { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPlayer = p }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = (if (selectedPlayer?.id == p.id) "◉ " else "○ ") + "#${p.jerseyNumber} ${p.fullName}",
                                    fontWeight = if (selectedPlayer?.id == p.id) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedPlayer?.id == p.id) PerlaGreenPrimary else Color.DarkGray
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Goles: $goals")
                        Row {
                            TextButton(onClick = { if (goals > 0) goals-- }) { Text("-") }
                            TextButton(onClick = { goals++ }) { Text("+") }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Asistencias: $assists")
                        Row {
                            TextButton(onClick = { if (assists > 0) assists-- }) { Text("-") }
                            TextButton(onClick = { assists++ }) { Text("+") }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Amarillas: $yellowCards")
                        Row {
                            TextButton(onClick = { if (yellowCards > 0) yellowCards-- }) { Text("-") }
                            TextButton(onClick = { if (yellowCards < 2) yellowCards++ }) { Text("+") }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Rojas: $redCards")
                        Row {
                            TextButton(onClick = { if (redCards > 0) redCards-- }) { Text("-") }
                            TextButton(onClick = { if (redCards < 1) redCards++ }) { Text("+") }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedPlayer?.let { p ->
                            val stat = MatchStat(
                                matchId = match.id,
                                playerId = p.id,
                                goals = goals,
                                assists = assists,
                                yellowCards = yellowCards,
                                redCards = redCards
                            )
                            viewModel.saveMatchStat(stat) {
                                showAddStatDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary)
                ) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStatDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
