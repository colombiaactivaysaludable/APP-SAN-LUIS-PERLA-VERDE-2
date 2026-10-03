package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import com.example.data.model.Player
import com.example.ui.MainViewModel
import com.example.ui.theme.CardRed
import com.example.ui.theme.CardYellow
import com.example.ui.theme.PerlaGoldAccent
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenPrimary

@Composable
fun StatsScreen(
    viewModel: MainViewModel,
    onSelectPlayer: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    val topScorers by viewModel.topScorers.collectAsStateWithLifecycle()
    val playerStatsSummaries by viewModel.playerStatsSummaries.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) } // 0: Goleadores, 1: Asistencias, 2: Tarjetas

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF9FBF9))) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("⚽ Goles", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("👟 Asistencias", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("🟨 Tarjetas", fontWeight = FontWeight.SemiBold) }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Goals Tab
                        val scorers = playerStatsSummaries.filter { it.totalGoals > 0 }.sortedByDescending { it.totalGoals }
                        if (scorers.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("Aún no hay goles registrados en los partidos jugados", color = Color.Gray)
                                }
                            }
                        }
                        items(scorers.withIndex().toList()) { (index, item) ->
                            RankingPlayerCard(
                                rank = index + 1,
                                player = item.player,
                                primaryMetric = "${item.totalGoals} Goles",
                                secondaryMetric = "${item.matchesPlayed} PJ • ${String.format("%.2f", if (item.matchesPlayed > 0) item.totalGoals.toDouble() / item.matchesPlayed else 0.0)} gol/partido",
                                badgeColor = when (index) {
                                    0 -> PerlaGoldAccent
                                    1 -> Color(0xFFC0C0C0)
                                    2 -> Color(0xFFCD7F32)
                                    else -> Color(0xFFE8F5E9)
                                },
                                onClick = { onSelectPlayer(item.player) }
                            )
                        }
                    }
                    1 -> {
                        // Assists Tab
                        val assisters = playerStatsSummaries.filter { it.totalAssists > 0 }.sortedByDescending { it.totalAssists }
                        if (assisters.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("Aún no hay asistencias registradas", color = Color.Gray)
                                }
                            }
                        }
                        items(assisters.withIndex().toList()) { (index, item) ->
                            RankingPlayerCard(
                                rank = index + 1,
                                player = item.player,
                                primaryMetric = "${item.totalAssists} Asistencias",
                                secondaryMetric = "${item.matchesPlayed} PJ",
                                badgeColor = Color(0xFFE8F5E9),
                                onClick = { onSelectPlayer(item.player) }
                            )
                        }
                    }
                    2 -> {
                        // Cards Tab
                        val carded = playerStatsSummaries.filter { it.yellowCards > 0 || it.redCards > 0 }
                            .sortedWith(compareByDescending<com.example.ui.PlayerStatsSummary> { it.redCards }.thenByDescending { it.yellowCards })
                        if (carded.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("¡Juego limpio! No hay tarjetas amarillas ni rojas registradas.", color = Color.Gray)
                                }
                            }
                        }
                        items(carded) { item ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth().clickable { onSelectPlayer(item.player) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(shape = CircleShape, color = PerlaGreenPrimary, modifier = Modifier.size(38.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("#${item.player.jerseyNumber}", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.player.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${item.matchesPlayed} PJ", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (item.yellowCards > 0) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = CardYellow) {
                                                Text("🟨 ${item.yellowCards}", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                        if (item.redCards > 0) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = CardRed) {
                                                Text("🟥 ${item.redCards}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankingPlayerCard(
    rank: Int,
    player: Player,
    primaryMetric: String,
    secondaryMetric: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = badgeColor,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$rank",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (badgeColor == PerlaGoldAccent) Color(0xFF5D4037) else PerlaGreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "#${player.jerseyNumber} ${player.fullName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = secondaryMetric,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Text(
                text = primaryMetric,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PerlaGreenPrimary
            )
        }
    }
}
