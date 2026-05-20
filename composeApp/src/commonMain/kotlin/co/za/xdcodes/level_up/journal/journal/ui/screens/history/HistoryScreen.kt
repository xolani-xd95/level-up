package com.trading.journal.ui.screens.history

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
import com.trading.journal.domain.model.TradeDirection
import com.trading.journal.domain.model.TradeStatus
import com.trading.journal.ui.components.TradeCard

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onTradeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val filter = state.filter

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text(
                "History",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 28.sp,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(12.dp))

            // Symbol search
            OutlinedTextField(
                value = filter.symbolQuery,
                onValueChange = { viewModel.updateFilter { copy(symbolQuery = it) } },
                label = { Text("Search symbol") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(10.dp))

            // Filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Direction
                item {
                    FilterChip(
                        selected = filter.direction == null,
                        onClick = { viewModel.updateFilter { copy(direction = null) } },
                        label = { Text("All") },
                    )
                }
                item {
                    FilterChip(
                        selected = filter.direction == TradeDirection.LONG,
                        onClick = { viewModel.updateFilter { copy(direction = TradeDirection.LONG) } },
                        label = { Text("Long") },
                    )
                }
                item {
                    FilterChip(
                        selected = filter.direction == TradeDirection.SHORT,
                        onClick = { viewModel.updateFilter { copy(direction = TradeDirection.SHORT) } },
                        label = { Text("Short") },
                    )
                }
                item { Spacer(Modifier.width(4.dp)) }
                // Status
                item {
                    FilterChip(
                        selected = filter.status == TradeStatus.OPEN,
                        onClick = {
                            viewModel.updateFilter {
                                copy(status = if (status == TradeStatus.OPEN) null else TradeStatus.OPEN)
                            }
                        },
                        label = { Text("Open") },
                    )
                }
                item {
                    FilterChip(
                        selected = filter.status == TradeStatus.CLOSED,
                        onClick = {
                            viewModel.updateFilter {
                                copy(status = if (status == TradeStatus.CLOSED) null else TradeStatus.CLOSED)
                            }
                        },
                        label = { Text("Closed") },
                    )
                }
            }
        }

        if (state.trades.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "No trades match your filters",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            items(state.trades, key = { it.id }) { trade ->
                TradeCard(
                    trade = trade,
                    onClick = { onTradeClick(trade.id) },
                    onDelete = { viewModel.delete(trade.id) },
                )
            }
        }
    }
}
