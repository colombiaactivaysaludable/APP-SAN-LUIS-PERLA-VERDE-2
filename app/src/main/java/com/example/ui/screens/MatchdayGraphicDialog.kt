package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.CallUp
import com.example.data.model.CallUpStatus
import com.example.data.model.Match
import com.example.data.model.Player
import com.example.ui.theme.PerlaGoldAccent
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenLight
import com.example.ui.theme.PerlaGreenPrimary
import com.example.ui.theme.PerlaGreenSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchdayGraphicDialog(
    match: Match,
    callUps: List<CallUp>,
    allPlayers: List<Player>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedTheme by remember { mutableStateOf("Verde Esmeralda") } // Verde Esmeralda, Oscuro Élite, Blanco Perla

    val matchCallUps = callUps.filter { it.matchId == match.id }
    val confirmedPlayerIds = matchCallUps
        .filter { it.status == CallUpStatus.CONFIRMADO }
        .map { it.playerId }
        .toSet()

    val confirmedPlayers = allPlayers
        .filter { it.id in confirmedPlayerIds }
        .sortedBy { it.jerseyNumber }

    val squadListText = if (confirmedPlayers.isNotEmpty()) {
        confirmedPlayers.joinToString("\n") { "• #${it.jerseyNumber} ${it.fullName} (${it.position.name})" }
    } else {
        allPlayers.take(16).joinToString("\n") { "• #${it.jerseyNumber} ${it.fullName} (${it.position.name})" }
    }

    val matchShareText = """
🟢⚪ MATCHDAY — COLONIA SAN LUIS ⚪🟢
🏆 ${if (match.tournament.isNotBlank()) match.tournament else "Torneo Amateur"}

🆚 COLONIA SAN LUIS vs ${match.displayOpponent}
📅 Fecha: ${match.date}
⏰ Hora: ${match.time}
📍 Cancha: ${match.venue}

📋 CONVOCATORIA CONFIRMADA (${if (confirmedPlayers.isNotEmpty()) confirmedPlayers.size else "Plantel"} jugadores):
$squadListText

${if (match.notes.isNotBlank()) "📌 Nota: ${match.notes}\n" else ""}¡Vamos Perla Verde por la victoria! ⚽🔥
    """.trimIndent()

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
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PerlaGreenDark)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🎨 Póster Oficial de Matchday",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Diseño gráfico para redes y WhatsApp",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }

                // Theme selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Estilo:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    listOf("Verde Esmeralda", "Oscuro Élite", "Blanco Perla").forEach { themeName ->
                        FilterChip(
                            selected = selectedTheme == themeName,
                            onClick = { selectedTheme = themeName },
                            label = { Text(themeName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PerlaGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Scrollable poster preview
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    val posterBackground = when (selectedTheme) {
                        "Oscuro Élite" -> Brush.verticalGradient(
                            listOf(Color(0xFF101B12), Color(0xFF1B2E1E), Color(0xFF0A110B))
                        )
                        "Blanco Perla" -> Brush.verticalGradient(
                            listOf(Color(0xFFFFFFFF), Color(0xFFF1F8F2), Color(0xFFE8F5E9))
                        )
                        else -> Brush.verticalGradient(
                            listOf(PerlaGreenDark, PerlaGreenPrimary, Color(0xFF134E1B))
                        )
                    }

                    val titleTextColor = when (selectedTheme) {
                        "Blanco Perla" -> PerlaGreenDark
                        else -> Color.White
                    }

                    val accentTextColor = when (selectedTheme) {
                        "Blanco Perla" -> PerlaGreenPrimary
                        else -> PerlaGoldAccent
                    }

                    val cardContentBg = when (selectedTheme) {
                        "Blanco Perla" -> Color.White.copy(alpha = 0.9f)
                        "Oscuro Élite" -> Color.Black.copy(alpha = 0.45f)
                        else -> Color.Black.copy(alpha = 0.25f)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("matchday_poster_card"),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        border = BorderStroke(2.dp, PerlaGoldAccent.copy(alpha = 0.6f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(posterBackground)
                                .padding(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Top Badge
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = PerlaGoldAccent,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                ) {
                                    Text(
                                        text = if (match.tournament.isNotBlank()) "🏆 ${match.tournament.uppercase()}" else "⚽ PRÓXIMO ENCUENTRO",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF1B3B1F),
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = "COLONIA SAN LUIS",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = titleTextColor,
                                    letterSpacing = 2.sp,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "PERLA VERDE FC",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentTextColor,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                // VS Box
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardContentBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Surface(
                                                shape = CircleShape,
                                                color = PerlaGreenPrimary,
                                                modifier = Modifier.size(46.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text("CSL", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("COLONIA", fontWeight = FontWeight.Bold, color = titleTextColor, fontSize = 12.sp)
                                        }

                                        Surface(
                                            shape = CircleShape,
                                            color = PerlaGoldAccent,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("VS", fontWeight = FontWeight.Black, color = Color(0xFF1B3B1F), fontSize = 11.sp)
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFF9E9E9E),
                                                modifier = Modifier.size(46.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(match.displayOpponent.uppercase(), fontWeight = FontWeight.Bold, color = titleTextColor, fontSize = 12.sp, maxLines = 1)
                                        }
                                    }
                                }

                                // Match Details
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardContentBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = accentTextColor, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(match.date, fontWeight = FontWeight.SemiBold, color = titleTextColor, fontSize = 13.sp)
                                            Spacer(modifier = Modifier.weight(1f))
                                            Icon(Icons.Default.Schedule, contentDescription = null, tint = accentTextColor, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(match.time, fontWeight = FontWeight.SemiBold, color = titleTextColor, fontSize = 13.sp)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = accentTextColor, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(match.venue, fontWeight = FontWeight.Medium, color = titleTextColor, fontSize = 12.sp)
                                        }
                                    }
                                }

                                // Squad preview
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardContentBg),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "📋 NÓMINA OFICIAL",
                                                fontWeight = FontWeight.Bold,
                                                color = accentTextColor,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = "${if (confirmedPlayers.isNotEmpty()) confirmedPlayers.size else allPlayers.size} Convocados",
                                                fontSize = 10.sp,
                                                color = titleTextColor.copy(alpha = 0.8f)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))

                                        val displayList = if (confirmedPlayers.isNotEmpty()) confirmedPlayers else allPlayers
                                        val firstBatch = displayList.take(14)
                                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                            firstBatch.chunked(2).forEach { rowPlayers ->
                                                Row(modifier = Modifier.fillMaxWidth()) {
                                                    rowPlayers.forEach { p ->
                                                        Text(
                                                            text = "#${p.jerseyNumber} ${p.fullName}",
                                                            color = titleTextColor,
                                                            fontSize = 11.sp,
                                                            modifier = Modifier.weight(1f),
                                                            maxLines = 1
                                                        )
                                                    }
                                                    if (rowPlayers.size == 1) {
                                                        Spacer(modifier = Modifier.weight(1f))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "¡JUNTOS POR LA GLORIA! 🟢⚪",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentTextColor
                                )
                            }
                        }
                    }
                }

                // Bottom Action Buttons
                Surface(
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, matchShareText)
                                    type = "text/plain"
                                    setPackage("com.whatsapp")
                                }
                                try {
                                    context.startActivity(sendIntent)
                                } catch (e: Exception) {
                                    val fallbackIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, matchShareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(fallbackIntent, "Compartir Matchday"))
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PerlaGreenPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("share_poster_whatsapp_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compartir por WhatsApp", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(matchShareText))
                                Toast.makeText(context, "Texto del partido copiado al portapapeles", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("copy_poster_text_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Copiar Texto Oficial")
                        }
                    }
                }
            }
        }
    }
}
