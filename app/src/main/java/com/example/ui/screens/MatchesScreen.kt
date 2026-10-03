package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CallUp
import com.example.data.model.CallUpStatus
import com.example.data.model.Match
import com.example.data.model.Player
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.theme.CardRed
import com.example.ui.theme.CardYellow
import com.example.ui.theme.PerlaGoldAccent
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenLight
import com.example.ui.theme.PerlaGreenPrimary
import com.example.ui.theme.PerlaGreenSecondary
import com.example.ui.theme.StatusActive
import java.net.URLEncoder

@Composable
fun MatchesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeTeam by viewModel.activeTeam.collectAsStateWithLifecycle()
    val allMatches by viewModel.allMatches.collectAsStateWithLifecycle()
    val allPlayers by viewModel.allPlayers.collectAsStateWithLifecycle()
    val allCallUps by viewModel.allCallUps.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Próximos, 1: Finalizados, 2: Todos
    var showNewMatchDialog by remember { mutableStateOf(false) }
    var matchToEdit by remember { mutableStateOf<Match?>(null) }
    var matchForConvocatoria by remember { mutableStateOf<Match?>(null) }
    var matchForBroadcastLink by remember { mutableStateOf<Match?>(null) }
    var matchForPublicConfirmation by remember { mutableStateOf<Match?>(null) }
    var matchForPoster by remember { mutableStateOf<Match?>(null) }
    var matchForStats by remember { mutableStateOf<Match?>(null) }

    val filteredMatches = remember(allMatches, selectedTab) {
        when (selectedTab) {
            0 -> allMatches.filter { !it.isPlayed }
            1 -> allMatches.filter { it.isPlayed }
            else -> allMatches
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF9FBF9))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PerlaGreenPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Próximos (${allMatches.count { !it.isPlayed }})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Finalizados (${allMatches.count { it.isPlayed }})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Todos (${allMatches.size})", fontWeight = FontWeight.Bold) }
                )
            }

            if (filteredMatches.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = PerlaGreenLight.copy(alpha = 0.4f),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = PerlaGreenPrimary, modifier = Modifier.size(36.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No hay partidos en esta sección", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
                        if (currentRole == UserRole.ADMIN || currentRole == UserRole.DT) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Toca el botón + para programar el próximo encuentro.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredMatches, key = { it.id }) { match ->
                        val matchCallUps = allCallUps.filter { it.matchId == match.id }
                        MatchCardItem(
                            match = match,
                            callUps = matchCallUps,
                            currentRole = currentRole,
                            onOpenConvocatoria = { matchForConvocatoria = match },
                            onOpenBroadcastLink = { matchForBroadcastLink = match },
                            onOpenPublicConfirmation = { matchForPublicConfirmation = match },
                            onOpenPoster = { matchForPoster = match },
                            onOpenStats = { matchForStats = match },
                            onEditMatch = { matchToEdit = match },
                            onDeleteMatch = { viewModel.deleteMatch(match) }
                        )
                    }
                }
            }
        }

        if (currentRole == UserRole.ADMIN || currentRole == UserRole.DT) {
            FloatingActionButton(
                onClick = { showNewMatchDialog = true },
                containerColor = PerlaGreenPrimary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("add_match_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Programar Partido")
            }
        }
    }

    // Dialogs
    if (showNewMatchDialog) {
        MatchEditDialog(
            match = null,
            teamId = activeTeam?.id ?: 1L,
            onDismiss = { showNewMatchDialog = false },
            onSave = { newMatch ->
                viewModel.saveMatch(newMatch) {
                    showNewMatchDialog = false
                }
            }
        )
    }

    matchToEdit?.let { match ->
        MatchEditDialog(
            match = match,
            teamId = activeTeam?.id ?: 1L,
            onDismiss = { matchToEdit = null },
            onSave = { updatedMatch ->
                viewModel.saveMatch(updatedMatch) {
                    matchToEdit = null
                }
            }
        )
    }

    matchForConvocatoria?.let { match ->
        ConvocatoriaDialog(
            match = match,
            callUps = allCallUps.filter { it.matchId == match.id },
            allPlayers = allPlayers,
            currentRole = currentRole,
            viewModel = viewModel,
            onOpenBroadcastLink = {
                matchForBroadcastLink = match
            },
            onOpenPublicConfirmation = {
                matchForPublicConfirmation = match
            },
            onOpenPoster = {
                matchForPoster = match
            },
            onDismiss = { matchForConvocatoria = null }
        )
    }

    matchForBroadcastLink?.let { match ->
        BroadcastLinkDialog(
            match = match,
            viewModel = viewModel,
            onOpenPublicConfirmation = {
                matchForPublicConfirmation = match
            },
            onDismiss = { matchForBroadcastLink = null }
        )
    }

    matchForPublicConfirmation?.let { match ->
        PublicMatchConfirmationDialog(
            match = match,
            viewModel = viewModel,
            onDismiss = { matchForPublicConfirmation = null }
        )
    }

    matchForPoster?.let { match ->
        MatchdayGraphicDialog(
            match = match,
            callUps = allCallUps.filter { it.matchId == match.id },
            allPlayers = allPlayers,
            onDismiss = { matchForPoster = null }
        )
    }

    matchForStats?.let { match ->
        MatchStatsDialog(
            match = match,
            callUps = allCallUps.filter { it.matchId == match.id },
            allPlayers = allPlayers,
            viewModel = viewModel,
            onDismiss = { matchForStats = null }
        )
    }
}

@Composable
fun MatchCardItem(
    match: Match,
    callUps: List<CallUp>,
    currentRole: UserRole,
    onOpenConvocatoria: () -> Unit,
    onOpenBroadcastLink: () -> Unit,
    onOpenPublicConfirmation: () -> Unit,
    onOpenPoster: () -> Unit,
    onOpenStats: () -> Unit,
    onEditMatch: () -> Unit,
    onDeleteMatch: () -> Unit
) {
    val confirmedCount = callUps.count { it.status == CallUpStatus.CONFIRMADO }
    val rejectedCount = callUps.count { it.status == CallUpStatus.RECHAZADO }
    val pendingCount = callUps.count { it.status == CallUpStatus.NO_CONFIRMADO }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("match_card_${match.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Tournament & Role Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (match.isPlayed) Color(0xFFEEEEEE) else PerlaGreenLight.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = if (match.tournament.isNotBlank()) match.tournament.uppercase() else "AMATEUR MATCH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (match.isPlayed) Color.DarkGray else PerlaGreenPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (currentRole == UserRole.ADMIN || currentRole == UserRole.DT) {
                    Row {
                        IconButton(onClick = onEditMatch, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                        if (currentRole == UserRole.ADMIN) {
                            IconButton(onClick = onDeleteMatch, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = CardRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Score or VS Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Colonia San Luis",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PerlaGreenPrimary
                    )
                    Text(
                        text = "vs ${match.displayOpponent}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }

                if (match.isPlayed) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PerlaGreenDark
                    ) {
                        Text(
                            text = "${match.goalsFor} - ${match.goalsAgainst}",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PerlaGoldAccent.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, PerlaGoldAccent)
                    ) {
                        Text(
                            text = "PRÓXIMO",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFF7A5800),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Date & Venue Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PerlaGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(match.date, fontSize = 12.sp, color = Color.DarkGray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = PerlaGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(match.time, fontSize = 12.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PerlaGreenPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(match.venue, fontSize = 12.sp, color = Color.DarkGray, maxLines = 1)
            }

            // Real-time confirmation counters chips (Requirement 3: Admin Dashboard View)
            if (!match.isPlayed && callUps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F8F2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = StatusActive, modifier = Modifier.size(10.dp)) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("$confirmedCount Confirmados", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusActive)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = CardRed, modifier = Modifier.size(10.dp)) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("$rejectedCount Bajas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CardRed)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = CardYellow, modifier = Modifier.size(10.dp)) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("$pendingCount Pendientes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB78103))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenConvocatoria,
                    colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("open_convocatoria_btn_${match.id}")
                ) {
                    Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Convocatoria", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (!match.isPlayed) {
                    OutlinedButton(
                        onClick = onOpenBroadcastLink,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("broadcast_link_btn_${match.id}")
                    ) {
                        Icon(Icons.Default.Link, contentDescription = "Enlace Único WhatsApp", tint = PerlaGreenPrimary, modifier = Modifier.size(16.dp))
                    }

                    OutlinedButton(
                        onClick = onOpenPoster,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("poster_btn_${match.id}")
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = "Póster de Matchday", tint = PerlaGreenPrimary, modifier = Modifier.size(16.dp))
                    }
                }

                if (currentRole == UserRole.ADMIN || currentRole == UserRole.DT) {
                    OutlinedButton(
                        onClick = onOpenStats,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("stats_btn_${match.id}")
                    ) {
                        Icon(Icons.Default.SportsSoccer, contentDescription = "Goles y Tarjetas", tint = PerlaGreenPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvocatoriaDialog(
    match: Match,
    callUps: List<CallUp>,
    allPlayers: List<Player>,
    currentRole: UserRole,
    viewModel: MainViewModel,
    onOpenBroadcastLink: () -> Unit = {},
    onOpenPublicConfirmation: () -> Unit = {},
    onOpenPoster: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showAddPlayerDialog by remember { mutableStateOf(false) }

    val confirmedCount = callUps.count { it.status == CallUpStatus.CONFIRMADO }
    val rejectedCount = callUps.count { it.status == CallUpStatus.RECHAZADO }
    val pendingCount = callUps.count { it.status == CallUpStatus.NO_CONFIRMADO }

    val callUpPlayerMap = callUps.associateBy { it.playerId }
    val convocatedPlayers = allPlayers.filter { it.id in callUpPlayerMap.keys }.sortedBy { it.jerseyNumber }
    val nonConvocatedPlayers = allPlayers.filter { it.id !in callUpPlayerMap.keys }.sortedBy { it.jerseyNumber }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .height(720.dp)
            .padding(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF9FBF9),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PerlaGreenDark)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📋 Convocatoria vs ${match.displayOpponent}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${match.date} • ${match.time} • ${match.venue}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Requirement 3: Admin Confirmation Dashboard Indicators
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📊 Control en Tiempo Real de Asistencia",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = PerlaGreenDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$confirmedCount", fontWeight = FontWeight.Black, fontSize = 20.sp, color = StatusActive)
                                        Text("Confirmados", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$rejectedCount", fontWeight = FontWeight.Black, fontSize = 20.sp, color = CardRed)
                                        Text("Bajas", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$pendingCount", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFFB78103))
                                        Text("Sin respuesta", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }

                                if (match.isFinalCallUpClosed) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFFF3E0),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "Convocatoria cerrada (Lista definitiva de partido generada)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFE65100)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Quick Action Buttons (Enlace WhatsApp, Vista Pública, Recordatorio a Pendientes)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onOpenBroadcastLink,
                                    colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Enlace WhatsApp", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = onOpenPublicConfirmation,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = PerlaGreenPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Vista Pública", fontSize = 12.sp)
                                }
                            }

                            // Reminder to Pending Players via WhatsApp (Requirement 3)
                            if (pendingCount > 0) {
                                val pendingPlayerNames = convocatedPlayers
                                    .filter { callUpPlayerMap[it.id]?.status == CallUpStatus.NO_CONFIRMADO }
                                    .joinToString(", ") { "#${it.jerseyNumber} ${it.fullName}" }

                                val reminderMsg = """
⚠️ RECORDATORIO CONVOCATORIA — COLONIA SAN LUIS
Partido: vs ${match.displayOpponent} (${match.date} ${match.time})

Jugadores con confirmación pendiente:
$pendingPlayerNames

Por favor ingresen al enlace a confirmar o justificar asistencia hoy mismo:
${match.getConfirmationLink()}
                                """.trimIndent()

                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, reminderMsg)
                                            type = "text/plain"
                                            setPackage("com.whatsapp")
                                        }
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            val fallback = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, reminderMsg)
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(fallback, "Recordar a pendientes"))
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB78103)),
                                    modifier = Modifier.fillMaxWidth().height(38.dp)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Recordar a $pendingCount pendientes por WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Requirement 4: Final Call Button (Cierre definitivo de convocatoria)
                            if (currentRole == UserRole.ADMIN || currentRole == UserRole.DT) {
                                Button(
                                    onClick = {
                                        val newClosedState = !match.isFinalCallUpClosed
                                        val updatedMatch = match.copy(
                                            isFinalCallUpClosed = newClosedState,
                                            excludeUnresponsivePlayers = newClosedState
                                        )
                                        viewModel.saveMatch(updatedMatch) {
                                            if (newClosedState) {
                                                // Exclude unconfirmed players automatically
                                                callUps.filter { it.status == CallUpStatus.NO_CONFIRMADO }.forEach { cu ->
                                                    viewModel.updateCallUpStatus(
                                                        callUp = cu,
                                                        status = CallUpStatus.RECHAZADO,
                                                        reason = "Excluido por cierre de convocatoria"
                                                    )
                                                }
                                                Toast.makeText(context, "Convocatoria cerrada. Jugadores no confirmados excluidos.", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Convocatoria reabierta para confirmaciones.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (match.isFinalCallUpClosed) Color(0xFF455A64) else Color(0xFFC2185B)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(38.dp)
                                ) {
                                    Icon(
                                        if (match.isFinalCallUpClosed) Icons.Default.LockOpen else Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        if (match.isFinalCallUpClosed) "Reabrir Convocatoria" else "Cerrar Convocatoria (Llamado Final)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Section: Players List
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Jugadores Convocados (${convocatedPlayers.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (currentRole == UserRole.ADMIN || currentRole == UserRole.DT) {
                                TextButton(onClick = { showAddPlayerDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Agregar", fontSize = 12.sp, color = PerlaGreenPrimary)
                                }
                            }
                        }
                    }

                    items(convocatedPlayers, key = { it.id }) { player ->
                        val callUp = callUpPlayerMap[player.id]
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = when (callUp?.status) {
                                            CallUpStatus.CONFIRMADO -> StatusActive.copy(alpha = 0.15f)
                                            CallUpStatus.RECHAZADO -> CardRed.copy(alpha = 0.15f)
                                            else -> CardYellow.copy(alpha = 0.2f)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "#${player.jerseyNumber}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = when (callUp?.status) {
                                                    CallUpStatus.CONFIRMADO -> StatusActive
                                                    CallUpStatus.RECHAZADO -> CardRed
                                                    else -> Color(0xFFB78103)
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(player.fullName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(
                                            text = when (callUp?.status) {
                                                CallUpStatus.CONFIRMADO -> "✓ Confirmado"
                                                CallUpStatus.RECHAZADO -> "✗ Ausente (${callUp.absenceReason?.ifBlank { "Sin motivo" } ?: "Sin motivo"})"
                                                else -> "⏳ Pendiente"
                                            },
                                            fontSize = 11.sp,
                                            color = when (callUp?.status) {
                                                CallUpStatus.CONFIRMADO -> StatusActive
                                                CallUpStatus.RECHAZADO -> CardRed
                                                else -> Color(0xFFB78103)
                                            }
                                        )
                                    }
                                }

                                // Admin / DT manual override toggles
                                if (currentRole == UserRole.ADMIN || currentRole == UserRole.DT) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = {
                                                callUp?.let {
                                                    viewModel.updateCallUpStatus(
                                                        callUp = it,
                                                        status = CallUpStatus.CONFIRMADO,
                                                        reason = null
                                                    )
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = "Confirmar", tint = StatusActive, modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(
                                            onClick = {
                                                callUp?.let {
                                                    viewModel.updateCallUpStatus(
                                                        callUp = it,
                                                        status = CallUpStatus.RECHAZADO,
                                                        reason = "Marcado por cuerpo técnico"
                                                    )
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Ausente", tint = CardRed, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom bar
                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 6.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onOpenPoster,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Póster Matchday", fontSize = 12.sp)
                        }

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Listo", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showAddPlayerDialog) {
        AlertDialog(
            onDismissRequest = { showAddPlayerDialog = false },
            title = { Text("Convocar Jugador al Partido") },
            text = {
                if (nonConvocatedPlayers.isEmpty()) {
                    Text("Todos los jugadores del plantel ya están convocados a este partido.")
                } else {
                    LazyColumn(modifier = Modifier.height(260.dp)) {
                        items(nonConvocatedPlayers) { player ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.addPlayersToMatch(match.id, listOf(player.id))
                                        showAddPlayerDialog = false
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#${player.jerseyNumber} ${player.fullName} (${player.position.name})", fontSize = 13.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddPlayerDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun MatchEditDialog(
    match: Match?,
    teamId: Long,
    onDismiss: () -> Unit,
    onSave: (Match) -> Unit
) {
    var opponent by remember { mutableStateOf(match?.opponent ?: "") }
    var tournament by remember { mutableStateOf(match?.tournament ?: "Torneo Oficial") }
    var date by remember { mutableStateOf(match?.date ?: "Sábado 24 Mayo") }
    var time by remember { mutableStateOf(match?.time ?: "16:00") }
    var venue by remember { mutableStateOf(match?.venue ?: "Cancha La Victoria") }
    var notes by remember { mutableStateOf(match?.notes ?: "") }
    var isPlayed by remember { mutableStateOf(match?.isPlayed ?: false) }
    var homeScore by remember { mutableStateOf(match?.goalsFor?.toString() ?: "0") }
    var awayScore by remember { mutableStateOf(match?.goalsAgainst?.toString() ?: "0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (match == null) "Nuevo Partido" else "Editar Partido", fontWeight = FontWeight.Bold, color = PerlaGreenPrimary) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = opponent,
                    onValueChange = { opponent = it },
                    label = { Text("Equipo Rival") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tournament,
                    onValueChange = { tournament = it },
                    label = { Text("Torneo o Competición") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Fecha") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Hora") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Cancha / Lugar") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Instrucciones o Notas") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = isPlayed,
                        onClick = { isPlayed = !isPlayed },
                        label = { Text(if (isPlayed) "Partido Finalizado" else "Partido Próximo") }
                    )
                }

                if (isPlayed) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = homeScore,
                            onValueChange = { homeScore = it },
                            label = { Text("Goles CSL") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = awayScore,
                            onValueChange = { awayScore = it },
                            label = { Text("Goles Rival") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalMatch = (match ?: Match(
                        teamId = teamId,
                        date = date.trim(),
                        time = time.trim(),
                        venue = venue.trim()
                    )).copy(
                        opponent = opponent.trim(),
                        tournament = tournament.trim(),
                        date = date.trim(),
                        time = time.trim(),
                        venue = venue.trim(),
                        notes = notes.trim(),
                        isPlayed = isPlayed,
                        goalsFor = homeScore.toIntOrNull() ?: 0,
                        goalsAgainst = awayScore.toIntOrNull() ?: 0
                    )
                    onSave(finalMatch)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary)
            ) {
                Text("Guardar Partido")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
