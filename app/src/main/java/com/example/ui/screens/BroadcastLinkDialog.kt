package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInBrowser
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.ui.MainViewModel
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenPrimary
import com.example.ui.theme.PerlaGreenSecondary
import java.net.URLEncoder

@Composable
fun BroadcastLinkDialog(
    match: Match,
    viewModel: MainViewModel,
    onOpenPublicConfirmation: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val confirmationLink = remember(match.id) {
        match.getConfirmationLink()
    }

    val rivalOrTournament = if (match.tournament.isNotBlank()) {
        "${match.displayOpponent} (${match.tournament})"
    } else {
        match.displayOpponent
    }

    val broadcastMessage = remember(match, confirmationLink) {
        """
🟢⚪ COLONIA SAN LUIS – PERLA VERDE ⚪🟢

📋 CONVOCATORIA — ${match.date}

🕐 Hora: ${match.time}
📍 Lugar: ${match.venue}
🆚 Rival: $rivalOrTournament

👉 Confirma tu asistencia aquí:
$confirmationLink

¡Vamos Colonia! 💪
        """.trimIndent()
    }

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
                        text = "Enlace Único de Convocatoria",
                        fontWeight = FontWeight.Bold,
                        color = PerlaGreenPrimary,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Para enviar por lista de difusión de WhatsApp",
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, Color(0xFFC8E6C9))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📢", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Envía este mensaje una sola vez a tu lista de difusión o grupo de WhatsApp. Cada jugador ingresa, selecciona su nombre y valida con los 2 últimos dígitos de su cédula.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PerlaGreenDark
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, PerlaGreenSecondary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = PerlaGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Enlace único del partido:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = PerlaGreenDark
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(confirmationLink))
                                    Toast.makeText(context, "🔗 Enlace copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Text(
                                text = confirmationLink,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = PerlaGreenPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mensaje oficial para WhatsApp:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(broadcastMessage))
                                    Toast.makeText(context, "📋 Mensaje copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = PerlaGreenPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF9FBF9),
                            border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = broadcastMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1B1D1B),
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = onOpenPublicConfirmation,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PerlaGreenPrimary),
                    border = BorderStroke(1.dp, PerlaGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("preview_public_confirmation_button")
                ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir pantalla del jugador (sin login)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val encoded = URLEncoder.encode(broadcastMessage, "UTF-8")
                        val uri = Uri.parse("https://api.whatsapp.com/send?text=$encoded")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        clipboardManager.setText(AnnotatedString(broadcastMessage))
                        Toast.makeText(context, "Copiado al portapapeles. Abre WhatsApp y pégalo en tu lista de difusión.", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("send_broadcast_whatsapp_button")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Enviar por WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(broadcastMessage))
                    Toast.makeText(context, "📋 Mensaje de difusión copiado", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copiar texto")
            }
        }
    )
}
