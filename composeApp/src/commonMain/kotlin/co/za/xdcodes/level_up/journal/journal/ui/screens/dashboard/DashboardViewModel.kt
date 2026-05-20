package com.trading.journal.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.journal.domain.model.Trade
import com.trading.journal.domain.model.TradeStats
import com.trading.journal.domain.repository.TradeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val stats: TradeStats = TradeStats.from(emptyList()),
    val recentTrades: List<Trade> = emptyList(),
    val cumulativePnl: List<Double> = emptyList(),
)

class DashboardViewModel(
    private val repository: TradeRepository,
) : ViewModel() {

    val uiState = repository.observeAll()
        .map { trades ->
            val closed = trades.filter { it.exitPrice != null }
                .sortedBy { it.date }
            var cum = 0.0
            val cumPnl = closed.map { t ->
                cum += t.pnl ?: 0.0
                cum
            }
            DashboardUiState(
                stats = TradeStats.from(trades),
                recentTrades = trades.take(5),
                cumulativePnl = cumPnl,
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DashboardUiState())
}
