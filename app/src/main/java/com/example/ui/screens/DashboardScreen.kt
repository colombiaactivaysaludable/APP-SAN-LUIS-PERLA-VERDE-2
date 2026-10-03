package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CallUpStatus
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.components.formatCurrencyCop
import com.example.ui.theme.CardRed
import com.example.ui.theme.CardYellow
import com.example.ui.theme.PerlaGoldAccent
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenPrimary
import com.example.ui.theme.PerlaGreenSecondary
import com.example.ui.theme.StatusActive

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToMatches: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToStats: () -> Unit,
    onAddNewMatch: () -> Unit,
    onOpenQrCode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeTeam by viewModel.activeTeam.collectAsStateWithLifecycle()
    val allMatches by viewModel.allMatches.collectAsStateWithLifecycle()
    val allPlayers by viewModel.allPlayers.collectAsStateWithLifecycle()
    val allCallUps by viewModel.allCallUps.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val debtors by viewModel.debtorsList.collectAsStateWithLifecycle()
    val totalPendingDebt by viewModel.totalPendingDebt.collectAsStateWithLifecycle()
    val totalCollected by viewModel.totalCollected.collectAsStateWithLifecycle()
    val topScorers by viewModel.topScorers.collectAsStateWithLifecycle()

    val nextMatch = allMatches.firstOrNull { !it.isPlayed }
    val nextMatchCallUps = nextMatch?.let { m -> allCallUps.filter { it.matchId == m.id } } ?: emptyList()
    val nextMatchConfirmed = nextMatchCallUps.count { it.status == CallUpStatus.CONFIRMADO }
    val nextMatchPending = nextMatchCallUps.count { it.status == CallUpStatus.NO_CONFIRMADO }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBF9)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card: Next Match & Call-Up Status
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PerlaGreenPrimary),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_next_match_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "PRÓXIMO PARTIDO",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (nextMatch != null) {
                            Text(
                                text = nextMatch.displayTournament,
                                style = MaterialTheme.typography.labelSmall,
                                color = PerlaGoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (nextMatch != null) {
                        Text(
                            text = "vs ${nextMatch.displayOpponent}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = nextMatch.date, color = Color.White, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = nextMatch.time, color = Color.White, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = nextMatch.venue, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Convocatoria Status Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Convocatoria en vivo", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                    Text(
                                        text = "$nextMatchConfirmed confirmados • $nextMatchPending pendientes",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Button(
                                    onClick = onNavigateToMatches,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Ver Convocatoria", color = PerlaGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No hay partidos programados",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Programa el próximo encuentro oficial o amistoso para generar el enlace de confirmación por WhatsApp.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (currentRole != UserRole.JUGADOR) {
                            Button(
                                onClick = onAddNewMatch,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = PerlaGreenPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Programar Partido", color = PerlaGreenPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Quick KPI Grid
        item {
            Text(
                text = "Resumen del Club",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PerlaGreenDark
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Players KPI
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToPlayers() }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = PerlaGreenPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Plantel", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${allPlayers.size} Jugadores",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${allPlayers.count { it.status == com.example.data.model.PlayerStatus.ACTIVO }} activos",
                            fontSize = 11.sp,
                            color = StatusActive
                        )
                    }
                }

                // Top Scorer KPI
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToStats() }
                ) {
                    val topScorer = topScorers.firstOrNull()
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF8E1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Goleador", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = topScorer?.let { "#${it.player.jerseyNumber} ${it.player.fullName.split(" ").first()}" } ?: "Sin goles",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = topScorer?.let { "${it.totalGoals} goles" } ?: "0 goles",
                            fontSize = 11.sp,
                            color = Color(0xFFF57F17),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Finances KPI Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pending Debt
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToPayments() }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Saldo por cobrar", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatCurrencyCop(totalPendingDebt),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (totalPendingDebt > 0) CardRed else StatusActive
                        )
                        Text(
                            text = "${debtors.count { it.pendingBalance > 0 }} con saldo",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Total Collected
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToPayments() }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Recaudado total", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatCurrencyCop(totalCollected),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = StatusActive
                        )
                        Text("Arbitrajes y cuotas", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }

        // Quick Action Buttons
        item {
            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PerlaGreenDark
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToMatches() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📋", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Partidos y Convocatorias", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Crear partido, enlace único y cartelera", fontSize = 12.sp, color = Color.Gray)
                        }
                        Text("➔", color = PerlaGreenPrimary)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = Color(0xFFEEEEEE)
                    ) {}

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenQrCode() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📲", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Compartir App con Jugadores (QR / APK)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Código QR y enlace directo para instalar en móviles", fontSize = 12.sp, color = Color.Gray)
                        }
                        Text("➔", color = PerlaGreenPrimary)
                    }
                }
            }
        }
    }
}
