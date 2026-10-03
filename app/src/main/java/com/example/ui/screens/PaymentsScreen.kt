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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.data.model.Payment
import com.example.data.model.PaymentConcept
import com.example.data.model.Player
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.components.formatCurrencyCop
import com.example.ui.theme.CardRed
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenPrimary
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusInjured
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaymentsScreen(
    viewModel: MainViewModel,
    onSelectPlayer: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    val allPayments by viewModel.allPayments.collectAsStateWithLifecycle()
    val allPlayers by viewModel.allPlayers.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val debtors by viewModel.debtorsList.collectAsStateWithLifecycle()
    val totalPendingDebt by viewModel.totalPendingDebt.collectAsStateWithLifecycle()
    val totalCollected by viewModel.totalCollected.collectAsStateWithLifecycle()

    var selectedSubTab by remember { mutableStateOf(0) } // 0: Deudores / Saldos, 1: Historial de Pagos
    var showAddPaymentDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF9FBF9))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header KPIs
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Por Cobrar", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatCurrencyCop(totalPendingDebt),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (totalPendingDebt > 0) CardRed else StatusActive
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE0E0E0)))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Recaudado Total", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatCurrencyCop(totalCollected),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = StatusActive
                        )
                    }
                }
            }

            TabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = Color.White
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = { Text("Estado por Jugador (${debtors.count { it.pendingBalance > 0 }} con saldo)", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = { Text("Historial de Pagos (${allPayments.size})", fontWeight = FontWeight.SemiBold) }
                )
            }

            if (selectedSubTab == 0) {
                // Debtors List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val sortedDebtors = debtors.sortedByDescending { it.pendingBalance }
                    items(sortedDebtors) { summary ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectPlayer(summary.player) }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (summary.pendingBalance > 0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#${summary.player.jerseyNumber}",
                                            fontWeight = FontWeight.Bold,
                                            color = if (summary.pendingBalance > 0) CardRed else StatusActive
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = summary.player.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = "Pagado: ${formatCurrencyCop(summary.totalPaid)} de ${formatCurrencyCop(summary.totalOwed)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (summary.pendingBalance > 0) formatCurrencyCop(summary.pendingBalance) else "Al día",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (summary.pendingBalance > 0) CardRed else StatusActive
                                    )
                                    if (summary.pendingBalance > 0) {
                                        Text("Debe", fontSize = 10.sp, color = StatusInjured)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Payments History List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (allPayments.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No hay registros de pago directos", color = Color.Gray)
                            }
                        }
                    }
                    items(allPayments) { pay ->
                        val player = allPlayers.find { it.id == pay.playerId }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (pay.isSettled) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Payment,
                                        contentDescription = null,
                                        tint = if (pay.isSettled) StatusActive else StatusInjured,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "#${player?.jerseyNumber ?: 0} ${player?.fullName ?: "Jugador"}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${pay.concept.label} • ${pay.date}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = formatCurrencyCop(pay.amountPaid),
                                        fontWeight = FontWeight.Bold,
                                        color = if (pay.isSettled) StatusActive else Color(0xFFE65100),
                                        fontSize = 14.sp
                                    )
                                    if (!pay.isSettled) {
                                        Text(
                                            text = "Debe: ${formatCurrencyCop(pay.pendingBalance)}",
                                            fontSize = 11.sp,
                                            color = StatusInjured
                                        )
                                    }
                                }

                                if (currentRole == UserRole.ADMIN) {
                                    IconButton(
                                        onClick = { viewModel.deletePayment(pay) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB to Record Payment
        if (currentRole == UserRole.ADMIN) {
            FloatingActionButton(
                onClick = { showAddPaymentDialog = true },
                containerColor = PerlaGreenPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("add_payment_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar Pago")
            }
        }
    }

    if (showAddPaymentDialog) {
        AddPaymentDialog(
            allPlayers = allPlayers,
            viewModel = viewModel,
            onDismiss = { showAddPaymentDialog = false }
        )
    }
}

@Composable
fun AddPaymentDialog(
    allPlayers: List<Player>,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var selectedPlayer by remember { mutableStateOf(allPlayers.firstOrNull()) }
    var concept by remember { mutableStateOf(PaymentConcept.ARBITRAJE) }
    var amountOwedText by remember { mutableStateOf("10000") }
    var amountPaidText by remember { mutableStateOf("10000") }
    var notes by remember { mutableStateOf("") }
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Cobro / Abono", fontWeight = FontWeight.Bold, color = PerlaGreenPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Seleccionar Jugador:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(allPlayers) { p ->
                        FilterChip(
                            selected = selectedPlayer?.id == p.id,
                            onClick = { selectedPlayer = p },
                            label = { Text("#${p.jerseyNumber} ${p.fullName.split(" ").first()}") }
                        )
                    }
                }

                Text("Concepto:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(PaymentConcept.values()) { c ->
                        FilterChip(
                            selected = concept == c,
                            onClick = { concept = c },
                            label = { Text(c.label) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amountOwedText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) amountOwedText = it },
                        label = { Text("Monto cobrado") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = amountPaidText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) amountPaidText = it },
                        label = { Text("Monto pagado") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas / Observaciones") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedPlayer?.let { p ->
                        val owed = amountOwedText.toDoubleOrNull() ?: 0.0
                        val paid = amountPaidText.toDoubleOrNull() ?: 0.0
                        val pay = Payment(
                            playerId = p.id,
                            concept = concept,
                            amountOwed = owed,
                            amountPaid = paid,
                            date = today,
                            notes = notes.trim()
                        )
                        viewModel.savePayment(pay) {
                            onDismiss()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary),
                enabled = selectedPlayer != null
            ) {
                Text("Guardar Registro")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
