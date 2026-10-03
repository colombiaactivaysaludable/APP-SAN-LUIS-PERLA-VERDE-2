package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.CardRed
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenPrimary
import com.example.ui.theme.PerlaGreenSecondary
import com.example.ui.theme.StatusActive
import kotlinx.coroutines.launch

@Composable
fun CloudSyncDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var importJsonText by remember { mutableStateOf("") }
    var isImporting by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sincronización y Respaldo",
                        fontWeight = FontWeight.Bold,
                        color = PerlaGreenPrimary,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Comparte o restaura los datos del club",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                }
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Export section
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = PerlaGreenPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "1. Exportar Respaldo de Datos",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = PerlaGreenDark
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Genera un archivo seguro con todos los jugadores, partidos, convocatorias y pagos para enviar por WhatsApp o guardar en Drive.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        val json = viewModel.syncManager.exportTeamDataJson(viewModel.activeTeamId.value)
                                        viewModel.syncManager.shareDataBackupIntent(json)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("export_backup_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Compartir Copia de Seguridad", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Import section
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, tint = PerlaGreenSecondary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "2. Restaurar Copia de Seguridad",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = PerlaGreenDark
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pega aquí el contenido JSON compartido por otro administrador para actualizar este dispositivo:",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = importJsonText,
                                onValueChange = { importJsonText = it },
                                label = { Text("Pegar contenido JSON") },
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth().testTag("input_import_json")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val clipText = clipboardManager.getText()?.text
                                        if (!clipText.isNullOrBlank()) {
                                            importJsonText = clipText
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Pegar portapapeles", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        if (importJsonText.isBlank()) {
                                            statusMessage = "Por favor ingresa o pega el JSON de respaldo."
                                            isSuccess = false
                                            return@Button
                                        }
                                        scope.launch {
                                            isImporting = true
                                            val result = viewModel.syncManager.importTeamDataJson(importJsonText)
                                            isImporting = false
                                            result.onSuccess { count ->
                                                statusMessage = "✅ ¡Sincronización exitosa! Se actualizaron $count registros."
                                                isSuccess = true
                                                importJsonText = ""
                                            }.onFailure { err ->
                                                statusMessage = "❌ Error al procesar respaldo: ${err.localizedMessage}"
                                                isSuccess = false
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenSecondary),
                                    enabled = !isImporting,
                                    modifier = Modifier.weight(1f).testTag("import_backup_button")
                                ) {
                                    Text(if (isImporting) "Procesando..." else "Restaurar", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                statusMessage?.let { msg ->
                    item {
                        Text(
                            text = msg,
                            color = if (isSuccess) StatusActive else CardRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary)
            ) {
                Text("Listo")
            }
        }
    )
}
