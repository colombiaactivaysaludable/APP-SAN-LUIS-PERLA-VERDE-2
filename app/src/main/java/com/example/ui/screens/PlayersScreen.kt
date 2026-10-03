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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenPrimary
import com.example.ui.theme.PerlaGreenSecondary

@Composable
fun PlayersScreen(
    viewModel: MainViewModel,
    onSelectPlayer: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    val allPlayers by viewModel.allPlayers.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedPositionFilter by remember { mutableStateOf<PlayerPosition?>(null) }
    var showAddPlayerDialog by remember { mutableStateOf(false) }

    val filteredPlayers = allPlayers.filter { player ->
        val matchesSearch = player.fullName.contains(searchQuery, ignoreCase = true) ||
                player.nickname.contains(searchQuery, ignoreCase = true) ||
                player.jerseyNumber.toString().contains(searchQuery)
        val matchesPosition = selectedPositionFilter == null || player.position == selectedPositionFilter
        matchesSearch && matchesPosition
    }.sortedBy { it.jerseyNumber }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF9FBF9))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search and Filter Bar
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por nombre, apodo o dorsal #") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("search_players_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Position Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedPositionFilter == null,
                            onClick = { selectedPositionFilter = null },
                            label = { Text("Todos (${allPlayers.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PerlaGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(PlayerPosition.values()) { pos ->
                        val count = allPlayers.count { it.position == pos }
                        FilterChip(
                            selected = selectedPositionFilter == pos,
                            onClick = {
                                selectedPositionFilter = if (selectedPositionFilter == pos) null else pos
                            },
                            label = { Text("${pos.label} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PerlaGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Players List
            if (filteredPlayers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏃", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No se encontraron jugadores" else "No hay jugadores registrados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Agrega los jugadores de tu plantel con su número y cédula para las confirmaciones.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredPlayers, key = { it.id }) { player ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectPlayer(player) }
                                .testTag("player_card_${player.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Jersey Number Circle Badge
                                Surface(
                                    shape = CircleShape,
                                    color = PerlaGreenPrimary,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#${player.jerseyNumber}",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = player.fullName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (player.nickname.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "\"${player.nickname}\"",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = player.position.label,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = PerlaGreenSecondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (player.phoneNumber.isNotBlank()) {
                                            Text(
                                                text = " • 📱 ${player.phoneNumber}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }

                                PlayerStatusChip(status = player.status)
                            }
                        }
                    }
                }
            }
        }

        // FAB to Add Player
        if (currentRole == UserRole.ADMIN) {
            FloatingActionButton(
                onClick = { showAddPlayerDialog = true },
                containerColor = PerlaGreenPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("add_player_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Jugador")
            }
        }
    }

    if (showAddPlayerDialog) {
        AddPlayerDialog(
            viewModel = viewModel,
            onDismiss = { showAddPlayerDialog = false }
        )
    }
}

@Composable
fun AddPlayerDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var jerseyNumberText by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var documentNumber by remember { mutableStateOf("") }
    var selectedPosition by remember { mutableStateOf(PlayerPosition.VOLANTE) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Jugador en Plantel", fontWeight = FontWeight.Bold, color = PerlaGreenPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre completo *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_player_fullname")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        label = { Text("Apodo") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = jerseyNumberText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) jerseyNumberText = it },
                        label = { Text("Dorsal # *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_player_jersey")
                    )
                }

                OutlinedTextField(
                    value = documentNumber,
                    onValueChange = { if (it.all { c -> c.isDigit() }) documentNumber = it },
                    label = { Text("Cédula (para verificación 2 últimos dígitos) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_player_document")
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Teléfono / WhatsApp") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Posición en campo:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(PlayerPosition.values()) { pos ->
                        FilterChip(
                            selected = selectedPosition == pos,
                            onClick = { selectedPosition = pos },
                            label = { Text(pos.label) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && jerseyNumberText.isNotBlank()) {
                        val num = jerseyNumberText.toIntOrNull() ?: 0
                        val newPlayer = Player(
                            fullName = fullName.trim(),
                            nickname = nickname.trim(),
                            jerseyNumber = num,
                            position = selectedPosition,
                            phoneNumber = phoneNumber.trim(),
                            documentNumber = documentNumber.trim(),
                            notes = notes.trim()
                        )
                        viewModel.savePlayer(newPlayer) {
                            onDismiss()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary),
                enabled = fullName.isNotBlank() && jerseyNumberText.isNotBlank()
            ) {
                Text("Guardar Jugador")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
