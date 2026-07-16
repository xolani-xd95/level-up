package co.za.xdcodez.wealthbuilder.journal.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodez.wealthbuilder.common.monthId
import co.za.xdcodez.wealthbuilder.journal.domain.JournalRepository
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class DayDetailViewModel(
    private val repository: JournalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DayDetailState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<DayDetailNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun init(date: String, monthIndex: Int, year: Int) {
        val today = Clock.System
            .todayIn(TimeZone.currentSystemDefault())
            .toString()

        _state.update {
            it.copy(
                date = date,
                isToday = date == today
            )
        }
        loadData(date, monthIndex, year)
    }

    private fun loadData(date: String, monthIndex: Int, year: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val config = repository.getConfig()
            val monthlyTarget = repository.getMonthlyTarget(
                monthId(monthIndex, year)
            )

            // fetch all month trades to calculate monthly P&L so far
            // excluding today's trades for carry forward calculation
            val allMonthTrades = repository.getTrades(
                month = monthIndex,
                year = year
            )

            val monthlyPnlSoFar = allMonthTrades
                .filter { it.date < date && it.status != TradeStatus.OPEN }
                .sumOf { it.pnl }

            // fetch today's trades
            val trades = repository.getTradesForDate(date)

            _state.update {
                it.copy(
                    config = config,
                    monthlyTarget = monthlyTarget,
                    monthlyPnlSoFar = monthlyPnlSoFar,
                    trades = trades,
                    isLoading = false
                )
            }
        }
    }

    fun onAction(action: DayDetailActions) {
        when (action) {
            DayDetailActions.NavigateBack -> emitNavEvent(
                DayDetailNavigationEvent.NavigateBack
            )
            DayDetailActions.OnAddTrade -> emitNavEvent(
                DayDetailNavigationEvent.ToAddTrade
            )
            is DayDetailActions.OnTradeClicked -> emitNavEvent(
                DayDetailNavigationEvent.ToTradeDetail(action.trade)
            )
        }
    }

    fun refresh() {
        val state = _state.value
        val date = state.date
        if (date.isEmpty()) return
        val parts = date.split("-")
        val year = parts[0].toIntOrNull() ?: return
        val month = parts[1].toIntOrNull() ?: return
        loadData(date, month, year)
    }

    private fun emitNavEvent(event: DayDetailNavigationEvent) {
        viewModelScope.launch { _navigationEvent.emit(event) }
    }
}