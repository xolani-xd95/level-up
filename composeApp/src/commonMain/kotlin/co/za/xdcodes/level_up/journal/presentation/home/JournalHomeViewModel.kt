package co.za.xdcodes.level_up.journal.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodes.level_up.common.monthId
import co.za.xdcodes.level_up.journal.domain.JournalRepository
import co.za.xdcodes.level_up.journal.domain.model.DailyPoint
import co.za.xdcodes.level_up.journal.domain.model.TradeDirection
import co.za.xdcodes.level_up.journal.domain.model.TradeEntry
import co.za.xdcodes.level_up.journal.domain.model.TradeStatus
import co.za.xdcodes.level_up.journal.domain.model.WeekDayStatus
import co.za.xdcodes.level_up.journal.presentation.home.JournalHomeNavigationEvent.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
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
            JournalHomeActions.OnSetupJournal -> emitNavEvent(
                ToSetup(
                    monthIndex = _state.value.selectedMonthIndex,
                    year = _state.value.selectedYear
                )
            )
            is JournalHomeActions.OnDayClicked -> {
                if (action.day.status != WeekDayStatus.FUTURE) {
                    emitNavEvent(
                        ToDayDetail(
                            date = action.day.date,
                            monthIndex = _state.value.selectedMonthIndex,
                            year =_state.value.selectedYear
                        )
                    )
                }
            }
            JournalHomeActions.Refresh -> loadData()
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
                dailyPoints = emptyList(),
                monthlyTarget = null,
                isLoading = true
            )
        }
        loadData()
    }

    fun seedTestData() {
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
            val monthIndex = _state.value.currentMonthIndex
            val year = _state.value.currentYear

            // add a few trades for today
            listOf(
                TradeEntry(
                    date = "2026-05-12",
                    direction = TradeDirection.LONG,
                    positionSize = 0.5,
                    entryPrice = 2310.00,
                    stopLoss = 2305.00,
                    takeProfit = 2320.00,
                    exitPrice = 2320.00,
                    pnl = 800.0,
                    status = TradeStatus.WIN,
                    notes = "Monday trade"
                ),
                TradeEntry(
                    date = "2026-05-13",
                    direction = TradeDirection.SHORT,
                    positionSize = 0.3,
                    entryPrice = 2325.00,
                    stopLoss = 2330.00,
                    takeProfit = 2310.00,
                    exitPrice = 2310.00,
                    pnl = 1200.0,
                    status = TradeStatus.WIN,
                    notes = "Tuesday trade"
                ),
                TradeEntry(
                    date = "2026-05-14",
                    direction = TradeDirection.LONG,
                    positionSize = 0.5,
                    entryPrice = 2320.50,
                    stopLoss = 2315.00,
                    takeProfit = 2331.00,
                    exitPrice = 2331.00,
                    pnl = 450.0,
                    status = TradeStatus.WIN,
                    notes = "Clean breakout setup"
                ),
                TradeEntry(
                    date = "2026-05-14",
                    direction = TradeDirection.SHORT,
                    positionSize = 0.3,
                    entryPrice = 2335.00,
                    stopLoss = 2340.00,
                    takeProfit = 2320.00,
                    exitPrice = 2340.00,
                    pnl = -150.0,
                    status = TradeStatus.LOSS,
                    notes = "Stopped out"
                )
            ).forEach { trade ->
                repository.addTrade(trade)
            }

            // add daily points for this week
            val monday = today.let {
                val date = LocalDate.parse(it)
                date.minus(date.dayOfWeek.ordinal, DateTimeUnit.DAY)
            }

            listOf(
                DailyPoint(
                    date = monday.toString(),
                    earned = true,
                    reason = "Target hit"
                ),
                DailyPoint(
                    date = monday.plus(1, DateTimeUnit.DAY).toString(),
                    earned = true,
                    reason = "Target hit"
                ),
                DailyPoint(
                    date = monday.plus(2, DateTimeUnit.DAY).toString(),
                    earned = false,
                    reason = "Override after target"
                )
            ).forEach { point ->
                repository.saveDailyPoint(point)
            }

            // refresh screen
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val state = _state.value

            val config = repository.getConfig()
            val monthlyTarget = repository.getMonthlyTarget(
                monthId(state.selectedMonthIndex, state.selectedYear)
            )
            val allTrades = repository.getTrades(
                month = state.selectedMonthIndex,
                year = state.selectedYear
            )
            val dailyPoints = repository.getDailyPoints(
                month = state.selectedMonthIndex,
                year = state.selectedYear
            )

            _state.update {
                it.copy(
                    config = config,
                    isConfigured = config != null && monthlyTarget != null,
                    monthlyTarget = monthlyTarget,
                    allMonthTrades = allTrades,
                    dailyPoints = dailyPoints,
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