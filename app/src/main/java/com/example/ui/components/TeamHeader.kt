package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlayerStatus
import com.example.data.model.Team
import com.example.data.model.UserRole
import com.example.ui.theme.PerlaGoldAccent
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenPrimary
import com.example.ui.theme.PerlaGreenSecondary
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusInactive
import com.example.ui.theme.StatusInjured

@Composable
fun TeamHeader(
    activeTeam: Team?,
    allTeams: List<Team>,
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onOpenSyncModal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showRoleMenu by remember { mutableStateOf(false) }
    var showTeamMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PerlaGreenDark,
                        PerlaGreenPrimary
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team Badge & Name (Tappable for team switching)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { showTeamMenu = true }
                        .testTag("team_selector_trigger")
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activeTeam?.badgeIcon ?: "⚽",
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeTeam?.name ?: "Colonia San Luis",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "▼", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                        Text(
                            text = activeTeam?.category ?: "Mayores / Libre",
                            style = MaterialTheme.typography.labelSmall,
                            color = PerlaGoldAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                DropdownMenu(
                    expanded = showTeamMenu,
                    onDismissRequest = { showTeamMenu = false }
                ) {
                    allTeams.forEach { team ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(team.badgeIcon, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(team.name, fontWeight = if (team.id == activeTeam?.id) FontWeight.Bold else FontWeight.Normal)
                                        Text(team.category, style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontSize = 11.sp)
                                    }
                                }
                            },
                            onClick = {
                                onTeamSelected(team)
                                showTeamMenu = false
                            }
                        )
                    }
                }

                // Cloud Sync / Backup trigger
                IconButton(onClick = onOpenSyncModal, modifier = Modifier.testTag("cloud_sync_button")) {
                    Text("☁️", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Role Selector & Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Role Selector Button
                Box {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { showRoleMenu = true }
                            .testTag("role_selector_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = when (currentRole) {
                                    UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                    UserRole.DT -> Icons.Default.SportsScore
                                    UserRole.JUGADOR -> Icons.Default.Person
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Rol: ${currentRole.label}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "▼",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 9.sp
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false }
                    ) {
                        UserRole.values().forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = role.label,
                                            fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = when (role) {
                                                UserRole.ADMIN -> "Gestión total y administración"
                                                UserRole.DT -> "Convocatorias y táctica"
                                                UserRole.JUGADOR -> "Consulta de estado y partidos"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                            fontSize = 11.sp
                                        )
                                    }
                                },
                                onClick = {
                                    onRoleSelected(role)
                                    showRoleMenu = false
                                }
                            )
                        }
                    }
                }

                // Subtitle badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Oriente Antioqueño ⚽",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerStatusChip(status: PlayerStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        PlayerStatus.ACTIVO -> Pair(Color(0xFFE8F5E9), StatusActive)
        PlayerStatus.INACTIVO -> Pair(Color(0xFFEEEEEE), StatusInactive)
        PlayerStatus.LESIONADO -> Pair(Color(0xFFFFEBEE), StatusInjured)
        PlayerStatus.SANCIONADO -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = status.label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
