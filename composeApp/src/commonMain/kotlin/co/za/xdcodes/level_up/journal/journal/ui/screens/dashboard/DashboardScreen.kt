package com.trading.journal.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trading.journal.ui.components.*
import com.trading.journal.ui.theme.TradingColors

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onTradeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val stats = state.stats

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ── Header ──
        item {
            Text(
                "Portfolio",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 28.sp,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "${stats.totalTrades} trades · ${stats.openTrades} open",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // ── Total PnL hero ──
        item {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Total P&L",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp,
                    )
                    Spacer(Modifier.height(6.dp))
                    val pnl = stats.totalPnl
                    val sign = if (pnl >= 0) "+" else ""
                    Text(
                        text = "$sign$${(pnl)}",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                        color = if (pnl >= 0) TradingColors.Profit else TradingColors.Loss,
                    )
                    Spacer(Modifier.height(16.dp))
                    PnlLineChart(cumulativePnl = state.cumulativePnl)
                }
            }
        }

        // ── Stats grid ──
        item {
            SectionHeader("Performance")
            Spacer(Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    StatCard(
                        label = "WIN RATE",
                        value = "${(stats.winRate)}%",
                        valueColor = if (stats.winRate >= 50) TradingColors.Profit else TradingColors.Loss,
                        modifier = Modifier.width(130.dp),
                    )
                }
                item {
                    StatCard("TRADES", "${stats.closedTrades}", modifier = Modifier.width(100.dp))
                }
                item {
                    StatCard(
                        label = "AVG WIN",
                        value = "+${(stats.avgWin)}",
                        valueColor = TradingColors.Profit,
                        modifier = Modifier.width(120.dp),
                    )
                }
                item {
                    StatCard(
                        label = "AVG LOSS",
                        value = "${(stats.avgLoss)}",
                        valueColor = TradingColors.Loss,
                        modifier = Modifier.width(120.dp),
                    )
                }
                item {
                    val pf = if (stats.profitFactor == Double.MAX_VALUE) "∞" else "${(stats.profitFactor)}"
                    StatCard("PROF. FACTOR", pf, modifier = Modifier.width(130.dp))
                }
                item {
                    StatCard(
                        label = "BEST TRADE",
                        value = "+${(stats.bestTrade)}",
                        valueColor = TradingColors.Profit,
                        modifier = Modifier.width(130.dp),
                    )
                }
                item {
                    StatCard(
                        label = "WORST TRADE",
                        value = "${(stats.worstTrade)}",
                        valueColor = TradingColors.Loss,
                        modifier = Modifier.width(130.dp),
                    )
                }
                item {
                    StatCard(
                        "WIN STREAK",
                        "${stats.longestWinStreak}",
                        modifier = Modifier.width(110.dp),
                    )
                }
            }
        }

        // ── Recent trades ──
        if (state.recentTrades.isNotEmpty()) {
            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader("Recent trades")
            }
            items(state.recentTrades) { trade ->
                TradeCard(
                    trade = trade,
                    onClick = { onTradeClick(trade.id) },
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No trades yet", style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text("Tap + to log your first trade",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
