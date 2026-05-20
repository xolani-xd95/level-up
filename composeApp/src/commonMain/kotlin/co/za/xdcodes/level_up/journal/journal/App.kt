package com.trading.journal

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.trading.journal.domain.repository.InMemoryTradeRepository
import com.trading.journal.domain.repository.TradeRepository
import com.trading.journal.ui.screens.analytics.AnalyticsScreen
import com.trading.journal.ui.screens.analytics.AnalyticsViewModel
import com.trading.journal.ui.screens.dashboard.DashboardScreen
import com.trading.journal.ui.screens.dashboard.DashboardViewModel
import com.trading.journal.ui.screens.history.HistoryScreen
import com.trading.journal.ui.screens.history.HistoryViewModel
import com.trading.journal.ui.screens.logtrade.LogTradeScreen
import com.trading.journal.ui.screens.logtrade.LogTradeViewModel
import com.trading.journal.ui.theme.TradingJournalTheme

private sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Outlined.Home)
    object Log      : Screen("log",       "Log",       Icons.Outlined.Home)
    object History  : Screen("history",   "History",   Icons.Outlined.Home)
    object Analytics: Screen("analytics", "Analytics", Icons.Outlined.Home)
}

private val SCREENS = listOf(Screen.Dashboard, Screen.Log, Screen.History, Screen.Analytics)

// Simple manual DI — swap with Koin/Kodein in production
private object AppContainer {
    val repository: TradeRepository = InMemoryTradeRepository()
    val dashboardVm by lazy { DashboardViewModel(repository) }
    val historyVm   by lazy { HistoryViewModel(repository) }
    val analyticsVm by lazy { AnalyticsViewModel(repository) }
    fun logTradeVm(editId: String? = null) = LogTradeViewModel(repository, editId)
}

@Composable
fun App() {
    TradingJournalTheme {
        var currentRoute by remember { mutableStateOf(Screen.Dashboard.route) }
        var logTradeVm by remember { mutableStateOf(AppContainer.logTradeVm()) }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    SCREENS.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (screen.route == Screen.Log.route) {
                                    logTradeVm = AppContainer.logTradeVm()
                                }
                                currentRoute = screen.route
                            },
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                        )
                    }
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                when (currentRoute) {
                    Screen.Dashboard.route ->
                        DashboardScreen(
                            viewModel = AppContainer.dashboardVm,
                            onTradeClick = { /* Navigate to detail */ },
                        )
                    Screen.Log.route ->
                        LogTradeScreen(
                            viewModel = logTradeVm,
                            onSaved = { currentRoute = Screen.History.route },
                        )
                    Screen.History.route ->
                        HistoryScreen(
                            viewModel = AppContainer.historyVm,
                            onTradeClick = { /* Navigate to detail / edit */ },
                        )
                    Screen.Analytics.route ->
                        AnalyticsScreen(viewModel = AppContainer.analyticsVm)
                }
            }
        }
    }
}
