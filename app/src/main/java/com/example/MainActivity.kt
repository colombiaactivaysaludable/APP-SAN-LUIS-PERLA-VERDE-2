package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Player
import com.example.ui.MainViewModel
import com.example.ui.components.TeamHeader
import com.example.ui.screens.CloudSyncDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MatchEditDialog
import com.example.ui.screens.MatchesScreen
import com.example.ui.screens.PaymentsScreen
import com.example.ui.screens.PlayerDetailDialog
import com.example.ui.screens.PlayersScreen
import com.example.ui.screens.QrCodeInstallDialog
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.PerlaGreenDark
import com.example.ui.theme.PerlaGreenLight
import com.example.ui.theme.PerlaGreenPrimary
import com.example.ui.theme.PerlaVerdeTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PerlaVerdeTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    var currentNavIndex by remember { mutableIntStateOf(0) }
    var selectedPlayerForDetail by remember { mutableStateOf<Player?>(null) }
    var showCloudSyncDialog by remember { mutableStateOf(false) }
    var showQrInstallDialog by remember { mutableStateOf(false) }
    var showQuickNewMatchDialog by remember { mutableStateOf(false) }

    val activeTeam by viewModel.activeTeam.collectAsStateWithLifecycle()
    val allTeams by viewModel.allTeams.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val allPayments by viewModel.allPayments.collectAsStateWithLifecycle()
    val allStats by viewModel.allStats.collectAsStateWithLifecycle()
    val allMatches by viewModel.allMatches.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TeamHeader(
                activeTeam = activeTeam,
                allTeams = allTeams,
                currentRole = currentRole,
                onRoleSelected = { viewModel.setRole(it) },
                onTeamSelected = { viewModel.setActiveTeam(it) },
                onOpenSyncModal = { showCloudSyncDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {
                val navItems = listOf(
                    Triple(0, "Inicio", Icons.Default.Home),
                    Triple(1, "Partidos", Icons.Default.SportsSoccer),
                    Triple(2, "Plantel", Icons.Default.People),
                    Triple(3, "Cuotas", Icons.Default.Payments),
                    Triple(4, "Estadísticas", Icons.Default.Assessment)
                )

                navItems.forEach { (index, label, icon) ->
                    NavigationBarItem(
                        selected = currentNavIndex == index,
                        onClick = { currentNavIndex = index },
                        icon = { Icon(icon, contentDescription = label) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (currentNavIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PerlaGreenPrimary,
                            selectedTextColor = PerlaGreenPrimary,
                            indicatorColor = PerlaGreenLight.copy(alpha = 0.5f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_item_$index")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentNavIndex) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToMatches = { currentNavIndex = 1 },
                    onNavigateToPlayers = { currentNavIndex = 2 },
                    onNavigateToPayments = { currentNavIndex = 3 },
                    onNavigateToStats = { currentNavIndex = 4 },
                    onAddNewMatch = { showQuickNewMatchDialog = true },
                    onOpenQrCode = { showQrInstallDialog = true }
                )
                1 -> MatchesScreen(
                    viewModel = viewModel
                )
                2 -> PlayersScreen(
                    viewModel = viewModel,
                    onSelectPlayer = { selectedPlayerForDetail = it }
                )
                3 -> PaymentsScreen(
                    viewModel = viewModel,
                    onSelectPlayer = { selectedPlayerForDetail = it }
                )
                4 -> StatsScreen(
                    viewModel = viewModel,
                    onSelectPlayer = { selectedPlayerForDetail = it }
                )
            }
        }
    }

    // Detail & Tool Dialogs
    selectedPlayerForDetail?.let { player ->
        PlayerDetailDialog(
            player = player,
            viewModel = viewModel,
            onDismiss = { selectedPlayerForDetail = null }
        )
    }

    if (showCloudSyncDialog) {
        CloudSyncDialog(
            viewModel = viewModel,
            onDismiss = { showCloudSyncDialog = false }
        )
    }

    if (showQrInstallDialog) {
        QrCodeInstallDialog(
            onDismiss = { showQrInstallDialog = false }
        )
    }

    if (showQuickNewMatchDialog) {
        MatchEditDialog(
            match = null,
            teamId = activeTeam?.id ?: 1L,
            onDismiss = { showQuickNewMatchDialog = false },
            onSave = { newMatch ->
                viewModel.saveMatch(newMatch) {
                    showQuickNewMatchDialog = false
                    currentNavIndex = 1
                }
            }
        )
    }
}
