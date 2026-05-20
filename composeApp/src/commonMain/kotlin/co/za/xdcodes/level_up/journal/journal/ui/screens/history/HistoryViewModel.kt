package com.trading.journal.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.journal.domain.model.Trade
import com.trading.journal.domain.model.TradeDirection
import com.trading.journal.domain.model.TradeStatus
import com.trading.journal.domain.repository.TradeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HistoryFilter(
    val symbolQuery: String = "",
    val direction: TradeDirection? = null,
    val status: TradeStatus? = null,
)

data class HistoryUiState(
    val trades: List<Trade> = emptyList(),
    val filter: HistoryFilter = HistoryFilter(),
)

class HistoryViewModel(
    private val repository: TradeRepository,
) : ViewModel() {

    private val _filter = MutableStateFlow(HistoryFilter())

    val uiState: StateFlow<HistoryUiState> = combine(
        repository.observeAll(), _filter,
    ) { trades, filter ->
        val filtered = trades.filter { t ->
            (filter.symbolQuery.isBlank() || t.symbol.contains(filter.symbolQuery.uppercase())) &&
            (filter.direction == null || t.direction == filter.direction) &&
            (filter.status == null || t.status == filter.status)
        }
        HistoryUiState(trades = filtered, filter = filter)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HistoryUiState())

    fun updateFilter(block: HistoryFilter.() -> HistoryFilter) = _filter.update(block)

    fun delete(id: String) = viewModelScope.launch { repository.delete(id) }
}
