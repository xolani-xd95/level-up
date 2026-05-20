package com.trading.journal.ui.screens.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.journal.domain.model.Trade
import com.trading.journal.domain.model.TradeStats
import com.trading.journal.domain.model.TradeStatus
import com.trading.journal.domain.repository.TradeRepository
import com.trading.journal.ui.components.SectionHeader
import com.trading.journal.ui.components.StatCard
import com.trading.journal.ui.theme.TradingColors
import kotlinx.coroutines.flow.*

// ── ViewModel ──────────────────────────────────────────────────────────────

data class AnalyticsUiState(
    val stats: TradeStats = TradeStats.from(emptyList()),
    val strategyWinRates: Map<String, Pair<Int, Int>> = emptyMap(), // strategy -> wins, total
)

class AnalyticsViewModel(repository: TradeRepository) : ViewModel() {
    val uiState = repository.observeAll().map { trades ->
        val strats = mutableMapOf<String, Pair<Int, Int>>()
//       Int trades.filter { it.status == TradeStatus.CLOSED }.forEach { t ->
//            val key = t.strategy.ifBlank { "Other" }
//            val (w, total) = strats.getOrDefault(key, 0 to 0)
//            strats[key] = (if ((t.pnl ?: 0.0) >= 0) w + 1 else w) to (total + 1)
//        }
        AnalyticsUiState(stats = TradeStats.from(trades), strategyWinRates = strats)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AnalyticsUiState())
}

// ── Screen ─────────────────────────────────────────────────────────────────

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val stats = state.stats

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                "Analytics",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 28.sp,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        // ── Key metrics 2x2 grid ──
        item {
            SectionHeader("Key metrics")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    label = "WIN RATE",
                    value = "${(stats.winRate)}%",
                    valueColor = if (stats.winRate >= 50) TradingColors.Profit else TradingColors.Loss,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "PROFIT FACTOR",
                    value = if (stats.profitFactor == Double.MAX_VALUE) "∞" else "${(stats.profitFactor)}",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    label = "AVG WIN",
                    value = "+${(stats.avgWin)}",
                    valueColor = TradingColors.Profit,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "AVG LOSS",
                    value = "${(stats.avgLoss)}",
                    valueColor = TradingColors.Loss,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    label = "WIN STREAK",
                    value = "${stats.longestWinStreak}",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "LOSS STREAK",
                    value = "${stats.longestLossStreak}",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ── Strategy bar chart ──
        item {
            if (state.strategyWinRates.isNotEmpty()) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader("Win rate by strategy")
                        Spacer(Modifier.height(16.dp))
                        StrategyBarChart(strategyWinRates = state.strategyWinRates)
                    }
                }
            }
        }

        // ── Trade distribution ──
        item {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader("Trade summary")
                    Spacer(Modifier.height(12.dp))
                    SummaryRow("Total trades", "${stats.totalTrades}")
                    SummaryRow("Closed", "${stats.closedTrades}")
                    SummaryRow("Open", "${stats.openTrades}")
                    SummaryRow("Wins", "${stats.wins}")
                    SummaryRow("Losses", "${stats.losses}")
                    SummaryRow("Best trade", "+${(stats.bestTrade)}")
                    SummaryRow("Worst trade", "${(stats.worstTrade)}")
                    SummaryRow("Avg R:R", "${(stats.avgRiskReward)}")
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface)
    }
    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
}

@Composable
private fun StrategyBarChart(strategyWinRates: Map<String, Pair<Int, Int>>) {
    val barColor = TradingColors.Profit
    val bgColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        strategyWinRates.entries.forEach { (strategy, pair) ->
            val (wins, total) = pair
            val rate = if (total == 0) 0f else wins.toFloat() / total
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = strategy,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(100.dp),
                    maxLines = 1,
                )
                Canvas(modifier = Modifier.weight(1f).height(12.dp)) {
                    val w = size.width; val h = size.height
                    drawRoundRect(bgColor, size = Size(w, h), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f))
                    if (rate > 0f)
                        drawRoundRect(barColor, size = Size(w * rate, h), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f))
                }
                Text(
                    text = "${(rate * 100)}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(36.dp),
                )
                Text(
                    text = "$wins/$total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(32.dp),
                )
            }
        }
    }
}
