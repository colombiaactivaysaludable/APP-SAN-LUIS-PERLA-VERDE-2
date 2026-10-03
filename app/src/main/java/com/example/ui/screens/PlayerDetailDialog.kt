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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Player
import com.example.data.model.PlayerPosition
import com.example.data.model.PlayerStatus
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.components.PlayerStatusChip
import com.example.ui.components.formatCurrencyCop
import com.example.ui.theme.CardRed
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenPrimary
import com.example.ui.theme.StatusActive

@Composable
fun PlayerDetailDialog(
    player: Player,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val allStats by viewModel.allStats.collectAsStateWithLifecycle()
    val allPayments by viewModel.allPayments.collectAsStateWithLifecycle()
    val debtors by viewModel.debtorsList.collectAsStateWithLifecycle()

    val playerStats = allStats.filter { it.playerId == player.id }
    val totalGoals = playerStats.sumOf { it.goals }
    val totalAssists = playerStats.sumOf { it.assists }
    val yellowCards = playerStats.sumOf { it.yellowCards }
    val redCards = playerStats.sumOf { it.redCards }

    val financialSummary = debtors.find { it.player.id == player.id }

    var isEditing by remember { mutableStateOf(false) }
    var editFullName by remember { mutableStateOf(player.fullName) }
    var editNickname by remember { mutableStateOf(player.nickname) }
    var editJerseyNumber by remember { mutableStateOf(player.jerseyNumber.toString()) }
    var editPhone by remember { mutableStateOf(player.phoneNumber) }
    var editDocument by remember { mutableStateOf(player.documentNumber) }
    var editPosition by remember { mutableStateOf(player.position) }
    var editStatus by remember { mutableStateOf(player.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "Editar Ficha de Jugador" else "Ficha del Jugador",
                    fontWeight = FontWeight.Bold,
                    color = PerlaGreenPrimary,
                    fontSize = 18.sp
                )

                if (currentRole == UserRole.ADMIN && !isEditing) {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = PerlaGreenPrimary)
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isEditing) {
                    // Header Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PerlaGreenPrimary,
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "#${player.jerseyNumber}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = player.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (player.nickname.isNotBlank()) {
                                    Text(text = "\"${player.nickname}\"", color = Color.Gray, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(player.position.label, color = PerlaGreenPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    PlayerStatusChip(status = player.status)
                                }
                            }
                        }
                    }

                    // Identification Details
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Datos de Identificación y Contacto", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PerlaGreenDark)
                            Text("Cédula: ${if (player.documentNumber.isNotBlank()) player.documentNumber else "No registrada"}")
                            Text("Dígitos de validación rápida: [ ${player.lastTwoDigitsOfId} ]", color = PerlaGreenPrimary, fontWeight = FontWeight.Bold)
                            Text("Teléfono / WhatsApp: ${if (player.phoneNumber.isNotBlank()) player.phoneNumber else "No registrado"}")
                        }
                    }

                    // Sports Stats
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Estadísticas en Temporada", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PerlaGreenDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("⚽ $totalGoals", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Goles", fontSize = 11.sp, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("👟 $totalAssists", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Asistencias", fontSize = 11.sp, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🟨 $yellowCards", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Amarillas", fontSize = 11.sp, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🟥 $redCards", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Rojas", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    // Financial summary
                    financialSummary?.let { fin ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Estado de Cuenta", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PerlaGreenDark)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total Cobrado:", fontSize = 12.sp, color = Color.Gray)
                                    Text(formatCurrencyCop(fin.totalOwed), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total Pagado:", fontSize = 12.sp, color = Color.Gray)
                                    Text(formatCurrencyCop(fin.totalPaid), color = StatusActive, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Saldo Pendiente:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        formatCurrencyCop(fin.pendingBalance),
                                        color = if (fin.pendingBalance > 0) CardRed else StatusActive,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    if (currentRole == UserRole.ADMIN) {
                        OutlinedButton(
                            onClick = {
                                viewModel.deletePlayer(player) {
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CardRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Eliminar Jugador", color = CardRed)
                        }
                    }
                } else {
                    // Edit Form
                    OutlinedTextField(
                        value = editFullName,
                        onValueChange = { editFullName = it },
                        label = { Text("Nombre completo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editNickname,
                        onValueChange = { editNickname = it },
                        label = { Text("Apodo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editJerseyNumber,
                        onValueChange = { if (it.all { c -> c.isDigit() }) editJerseyNumber = it },
                        label = { Text("Dorsal #") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDocument,
                        onValueChange = { if (it.all { c -> c.isDigit() }) editDocument = it },
                        label = { Text("Cédula de ciudadanía") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Teléfono / WhatsApp") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Posición:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(PlayerPosition.values()) { pos ->
                            FilterChip(
                                selected = editPosition == pos,
                                onClick = { editPosition = pos },
                                label = { Text(pos.label) }
                            )
                        }
                    }

                    Text("Estado:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(PlayerStatus.values()) { st ->
                            FilterChip(
                                selected = editStatus == st,
                                onClick = { editStatus = st },
                                label = { Text(st.label) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isEditing) {
                Button(
                    onClick = {
                        val num = editJerseyNumber.toIntOrNull() ?: player.jerseyNumber
                        val updated = player.copy(
                            fullName = editFullName.trim(),
                            nickname = editNickname.trim(),
                            jerseyNumber = num,
                            phoneNumber = editPhone.trim(),
                            documentNumber = editDocument.trim(),
                            position = editPosition,
                            status = editStatus
                        )
                        viewModel.savePlayer(updated) {
                            isEditing = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary)
                ) {
                    Text("Guardar Cambios")
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary)
                ) {
                    Text("Cerrar")
                }
            }
        },
        dismissButton = {
            if (isEditing) {
                TextButton(onClick = { isEditing = false }) {
                    Text("Cancelar")
                }
            }
        }
    )
}
