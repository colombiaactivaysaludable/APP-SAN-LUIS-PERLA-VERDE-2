package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CallUpStatus
import com.example.data.model.Match
import com.example.data.model.Player
import com.example.ui.MainViewModel
import com.example.ui.components.CallUpStatusChip
import com.example.ui.theme.CardRed
import com.example.ui.theme.PerlaGoldAccent
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenLight
import com.example.ui.theme.PerlaGreenPrimary
import com.example.ui.theme.PerlaGreenSecondary
import com.example.ui.theme.StatusActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicMatchConfirmationDialog(
    match: Match,
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    preselectedPlayerId: Long? = null
) {
    val context = LocalContext.current
    val allCallUps by viewModel.allCallUps.collectAsStateWithLifecycle()
    val allPlayers by viewModel.allPlayers.collectAsStateWithLifecycle()
    val activeTeam by viewModel.activeTeam.collectAsStateWithLifecycle()

    val matchCallUps = allCallUps.filter { it.matchId == match.id }
    val callUpPlayerMap = matchCallUps.associateBy { it.playerId }
    val convocatedPlayers = allPlayers.filter { it.id in callUpPlayerMap.keys }.sortedBy { it.jerseyNumber }

    var selectedPlayerForConfirm by remember {
        mutableStateOf(convocatedPlayers.find { it.id == preselectedPlayerId })
    }
    var showVerificationModal by remember { mutableStateOf(false) }
    var idLastTwoDigitsInput by remember { mutableStateOf("") }
    var verificationError by remember { mutableStateOf<String?>(null) }
    var isVerified by remember { mutableStateOf(false) }

    var selectedDecision by remember { mutableStateOf<Boolean?>(null) }
    var selectedAbsenceReason by remember { mutableStateOf("Trabajo") }
    var customAbsenceReason by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }
    val filteredConvocados = convocatedPlayers.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.jerseyNumber.toString().contains(searchQuery) ||
                it.nickname.contains(searchQuery, ignoreCase = true)
    }

    val confirmedCount = matchCallUps.count { it.status == CallUpStatus.CONFIRMADO }
    val rejectedCount = matchCallUps.count { it.status == CallUpStatus.RECHAZADO }
    val pendingCount = matchCallUps.count { it.status == CallUpStatus.NO_CONFIRMADO }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .height(720.dp)
            .padding(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFF9FBF9),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with Logo, Team Name, and Match Data
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(PerlaGreenDark, PerlaGreenPrimary)
                            )
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Image(
                                            painter = painterResource(id = R.drawable.perla_verde_icon_1790649640905),
                                            contentDescription = "Escudo Perla Verde",
                                            modifier = Modifier.size(34.dp).clip(CircleShape)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "COLONIA SAN LUIS",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Perla Verde • Convocatoria Oficial",
                                        color = PerlaGoldAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Match Details Banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "🆚 vs ${match.displayOpponent}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = match.date, color = Color.White, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = match.time, color = Color.White, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = match.venue, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Live Summary Counters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9),
                        border = BorderStroke(1.dp, Color(0xFF81C784)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("$confirmedCount", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2E7D32))
                            Text("Confirmados", fontSize = 10.sp, color = Color(0xFF1B5E20))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFEBEE),
                        border = BorderStroke(1.dp, Color(0xFFE57373)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("$rejectedCount", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFC62828))
                            Text("No van", fontSize = 10.sp, color = Color(0xFFB71C1C))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF8E1),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("$pendingCount", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFF57F17))
                            Text("Pendientes", fontSize = 10.sp, color = Color(0xFFE65100))
                        }
                    }
                }

                // Action instruction banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("👇", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Toca tu nombre en la lista para confirmar o rechazar tu asistencia:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = PerlaGreenPrimary
                        )
                    }
                }

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar mi nombre o dorsal #") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                )

                // Real-time Convocated Players List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredConvocados, key = { it.id }) { player ->
                        val callUp = callUpPlayerMap[player.id]
                        val status = callUp?.status ?: CallUpStatus.NO_CONFIRMADO

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            border = BorderStroke(
                                1.dp,
                                when (status) {
                                    CallUpStatus.CONFIRMADO -> StatusActive.copy(alpha = 0.5f)
                                    CallUpStatus.RECHAZADO -> CardRed.copy(alpha = 0.5f)
                                    CallUpStatus.NO_CONFIRMADO -> Color(0xFFE0E0E0)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlayerForConfirm = player
                                    idLastTwoDigitsInput = ""
                                    verificationError = null
                                    isVerified = false
                                    selectedDecision = null
                                    showVerificationModal = true
                                }
                                .testTag("public_player_item_${player.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = when (status) {
                                        CallUpStatus.CONFIRMADO -> Color(0xFFE8F5E9)
                                        CallUpStatus.RECHAZADO -> Color(0xFFFFEBEE)
                                        CallUpStatus.NO_CONFIRMADO -> Color(0xFFF5F5F5)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#${player.jerseyNumber}",
                                            fontWeight = FontWeight.Bold,
                                            color = when (status) {
                                                CallUpStatus.CONFIRMADO -> Color(0xFF2E7D32)
                                                CallUpStatus.RECHAZADO -> CardRed
                                                CallUpStatus.NO_CONFIRMADO -> Color.DarkGray
                                            },
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = player.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = player.position.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }

                                CallUpStatusChip(status = status, reason = callUp?.absenceReason)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal de Confirmación y Verificación de 2 dígitos de cédula
    if (showVerificationModal && selectedPlayerForConfirm != null) {
        val player = selectedPlayerForConfirm!!
        val currentCallUp = callUpPlayerMap[player.id]

        AlertDialog(
            onDismissRequest = { showVerificationModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = PerlaGreenPrimary, modifier = Modifier.size(36.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("#${player.jerseyNumber}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(player.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Confirmación de Asistencia", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!isVerified) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F8E9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = PerlaGreenPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ingresa los 2 últimos dígitos de tu cédula para validar tu identidad sin contraseña:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PerlaGreenDark
                                )
                            }
                        }

                        OutlinedTextField(
                            value = idLastTwoDigitsInput,
                            onValueChange = {
                                if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                    idLastTwoDigitsInput = it
                                    verificationError = null
                                }
                            },
                            label = { Text("Últimos 2 dígitos") },
                            placeholder = { Text("Ej: 45") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            isError = verificationError != null,
                            modifier = Modifier.fillMaxWidth().testTag("input_verification_2digits")
                        )

                        verificationError?.let { err ->
                            Text(text = err, color = CardRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        // Paso de decisión: Sí / No
                        Text(
                            text = "¿Asistirás al partido este ${match.date} vs ${match.displayOpponent}?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { selectedDecision = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedDecision == true) StatusActive else Color(0xFFE8F5E9),
                                    contentColor = if (selectedDecision == true) Color.White else StatusActive
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(48.dp).testTag("button_confirm_yes")
                            ) {
                                Text("✅ Sí, voy", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { selectedDecision = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedDecision == false) CardRed else Color(0xFFFFEBEE),
                                    contentColor = if (selectedDecision == false) Color.White else CardRed
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(48.dp).testTag("button_confirm_no")
                            ) {
                                Text("❌ No puedo", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Motivo si elige "No puedo ir"
                        AnimatedVisibility(visible = selectedDecision == false) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Motivo de inasistencia:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                val quickReasons = listOf("Trabajo", "Lesión", "Viaje", "Personal", "Otro")
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(quickReasons) { reason ->
                                        FilterChip(
                                            selected = selectedAbsenceReason == reason,
                                            onClick = { selectedAbsenceReason = reason },
                                            label = { Text(reason) }
                                        )
                                    }
                                }

                                if (selectedAbsenceReason == "Otro") {
                                    OutlinedTextField(
                                        value = customAbsenceReason,
                                        onValueChange = { customAbsenceReason = it },
                                        placeholder = { Text("Escribe el motivo brevemente") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (!isVerified) {
                    Button(
                        onClick = {
                            val expectedDigits = player.lastTwoDigitsOfId
                            if (idLastTwoDigitsInput == expectedDigits) {
                                isVerified = true
                                verificationError = null
                            } else {
                                verificationError = "Los dígitos no coinciden con los registrados en la ficha."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary),
                        enabled = idLastTwoDigitsInput.length == 2
                    ) {
                        Text("Verificar")
                    }
                } else {
                    Button(
                        onClick = {
                            selectedDecision?.let { decision ->
                                val finalReason = if (!decision) {
                                    if (selectedAbsenceReason == "Otro") customAbsenceReason.ifBlank { "Otro" } else selectedAbsenceReason
                                } else null

                                viewModel.confirmPlayerAttendance(
                                    matchId = match.id,
                                    playerId = player.id,
                                    confirmed = decision,
                                    absenceReason = finalReason
                                ) {
                                    Toast.makeText(
                                        context,
                                        if (decision) "✅ ¡Asistencia confirmada, ${player.fullName}!" else "Registrado: No asiste al partido",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showVerificationModal = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary),
                        enabled = selectedDecision != null
                    ) {
                        Text("Guardar Respuesta")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showVerificationModal = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
