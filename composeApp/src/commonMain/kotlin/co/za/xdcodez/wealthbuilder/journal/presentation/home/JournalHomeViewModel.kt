package co.za.xdcodez.wealthbuilder.journal.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodez.wealthbuilder.common.monthId
import co.za.xdcodez.wealthbuilder.journal.domain.JournalRepository
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradingConfig
import co.za.xdcodez.wealthbuilder.journal.domain.model.WeekDayStatus
import co.za.xdcodez.wealthbuilder.journal.presentation.home.JournalHomeNavigationEvent.ToDayDetail
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class JournalHomeViewModel(
    private val repository: JournalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(JournalHomeState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<JournalHomeNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        _state.update {
            it.copy(
                currentMonthIndex = today.monthNumber,
                currentYear = today.year,
                selectedMonthIndex = today.monthNumber,
                selectedYear = today.year
            )
        }
        loadData()
    }

    fun onAction(action: JournalHomeActions) {
        when (action) {
            JournalHomeActions.PreviousMonth -> navigateMonth(-1)
            JournalHomeActions.NextMonth -> navigateMonth(1)
            is JournalHomeActions.OnDayClicked -> {
                if (action.day.status != WeekDayStatus.FUTURE) {
                    emitNavEvent(
                        ToDayDetail(
                            date = action.day.date,
                            monthIndex = _state.value.selectedMonthIndex,
                            year = _state.value.selectedYear
                        )
                    )
                }
            }

            JournalHomeActions.Refresh -> loadData()
        }
    }

     fun saveConfig() {
        viewModelScope.launch {
            repository.saveConfig(TradingConfig())
        }
    }

    private fun navigateMonth(direction: Int) {
        val current = _state.value
        val newMonth = current.selectedMonthIndex + direction
        val (resolvedMonth, resolvedYear) = when {
            newMonth < 1 -> 12 to current.selectedYear - 1
            newMonth > 12 -> 1 to current.selectedYear + 1
            else -> newMonth to current.selectedYear
        }
        _state.update {
            it.copy(
                selectedMonthIndex = resolvedMonth,
                selectedYear = resolvedYear,
                allMonthTrades = emptyList(),
                monthlyTarget = null,
                isLoading = true
            )
        }
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val state = _state.value
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

            val config = repository.getConfig()
            val monthlyTarget = repository.getMonthlyTarget(
                monthId(state.selectedMonthIndex, state.selectedYear)
            )
            val allTrades = repository.getTrades(
                month = state.selectedMonthIndex,
                year = state.selectedYear
            )

            // Fetch all trades from cycle start to today for capped compounding
            val allCycleTrades = if (config != null && config.cycleStartDate.isNotEmpty()) {
                repository.getAllTradesInCycle(
                    startDate = config.cycleStartDate,
                    endDate = today
                )
            } else {
                emptyList()
            }

            // Fetch EA synced account balance
            val accountBalance = repository.getAccountBalance()

            _state.update {
                it.copy(
                    config = config,
                    isConfigured = config?.isConfigured == true,
                    monthlyTarget = monthlyTarget,
                    allMonthTrades = allTrades,
                    allCycleTrades = allCycleTrades,
                    accountBalance = accountBalance,
                    isLoading = false
                )
            }
        }
    }

    private fun emitNavEvent(event: JournalHomeNavigationEvent) {
        viewModelScope.launch {
            _navigationEvent.emit(event)
        }
    }
}