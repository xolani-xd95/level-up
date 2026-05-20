package co.za.xdcodes.level_up.journal.presentation.home

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import co.za.xdcodes.level_up.journal.data.model.MonthlyTradingTarget
import co.za.xdcodes.level_up.journal.domain.model.DailyPoint
import co.za.xdcodes.level_up.journal.domain.model.SessionStatus
import co.za.xdcodes.level_up.journal.domain.model.StopReason
import co.za.xdcodes.level_up.journal.domain.model.TradeEntry
import co.za.xdcodes.level_up.journal.domain.model.TradeStatus
import co.za.xdcodes.level_up.journal.domain.model.TradingConfig
import co.za.xdcodes.level_up.journal.domain.model.WeekDay
import co.za.xdcodes.level_up.journal.domain.model.WeekDayStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

data class JournalHomeState(
    // month navigation
    val currentMonthIndex: Int = 0,
    val currentYear: Int = 0,
    val selectedMonthIndex: Int = 0,
    val selectedYear: Int = 0,

    // config
    val config: TradingConfig? = null,
    val monthlyTarget: MonthlyTradingTarget? = null,
    val isConfigured: Boolean = false,
    val allMonthTrades: List<TradeEntry> = emptyList(),
    val dailyPoints: List<DailyPoint> = emptyList(),

    val isLoading: Boolean = false,
    val error: String? = null
) {
    // ── Month type ───────────────────────────────────────────────
    val isCurrentMonth: Boolean
        get() = selectedMonthIndex == currentMonthIndex &&
                selectedYear == currentYear

    val isPastMonth: Boolean
        get() = selectedYear < currentYear ||
                (selectedYear == currentYear &&
                        selectedMonthIndex < currentMonthIndex)

    val isFutureMonth: Boolean
        get() = selectedYear > currentYear ||
                (selectedYear == currentYear &&
                        selectedMonthIndex > currentMonthIndex)

    // ── Monthly ──────────────────────────────────────────────────
    val monthlyPnl: Double
        get() = allMonthTrades
            .filter { it.status != TradeStatus.OPEN }
            .sumOf { it.pnl }

    val monthlyProgress: Float
        get() {
            val target = monthlyTarget?.monthlyTarget ?: 0.0
            return if (target <= 0.0) 0f
            else (monthlyPnl / target).coerceIn(0.0, 1.0).toFloat()
        }

    // ── Discipline ───────────────────────────────────────────────
    val monthlyPoints: Int
        get() = dailyPoints.count { it.earned }

    val totalTradingDays: Int
        get() = dailyPoints.size

    val currentStreak: Int
        get() {
            var streak = 0
            for (point in dailyPoints.sortedByDescending { it.date }) {
                if (point.earned) streak++ else break
            }
            return streak
        }

    // ── Monthly Performance ─────────────────────────────────────────────
    val winRate: Float
        get() {
            val closed = allMonthTrades.filter { it.status != TradeStatus.OPEN }
            if (closed.isEmpty()) return 0f
            return (closed.count { it.status == TradeStatus.WIN }.toFloat() / closed.size) * 100
        }

    val profitFactor: Double
        get() {
            val totalWins = allMonthTrades
                .filter { it.status == TradeStatus.WIN }
                .sumOf { it.pnl }
            val totalLosses = allMonthTrades
                .filter { it.status == TradeStatus.LOSS }
                .sumOf { it.pnl }
            return if (totalLosses == 0.0) totalWins else totalWins / totalLosses
        }

    val bestDay: Double
        get() = allMonthTrades
            .groupBy { it.date }
            .map { (_, trades) -> trades.sumOf { it.pnl } }
            .maxOrNull() ?: 0.0

    val worstDay: Double
        get() = allMonthTrades
            .groupBy { it.date }
            .map { (_, trades) -> trades.sumOf { it.pnl } }
            .minOrNull() ?: 0.0

    val avgDailyPnl: Double
        get() {
            val tradingDays = allMonthTrades.map { it.date }.distinct()
            if (tradingDays.isEmpty()) return 0.0
            return monthlyPnl / tradingDays.size
        }

    val totalTrades: Int
        get() = allMonthTrades.size

    val biggestWin: Double
        get() = allMonthTrades
            .filter { it.status == TradeStatus.WIN }
            .maxOfOrNull { it.pnl } ?: 0.0

    val biggestLoss: Double
        get() = allMonthTrades
            .filter { it.status == TradeStatus.LOSS }
            .minOfOrNull { it.pnl } ?: 0.0

    // ── Weekly pills ─────────────────────────────────────────────
    val weekDays: List<WeekDay>
        get() {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val monday = today.minus(
                today.dayOfWeek.ordinal,
                DateTimeUnit.DAY
            )
            return (0..4).map { offset ->
                val date = monday.plus(offset, DateTimeUnit.DAY)
                val dateStr = date.toString()
                val point = dailyPoints.find { it.date == dateStr }
                val dayTrades = allMonthTrades.filter { it.date == dateStr }
                WeekDay(
                    label = date.dayOfWeek.name.take(3).uppercase(),
                    date = dateStr,
                    status = when {
                        date == today -> WeekDayStatus.TODAY
                        date > today -> WeekDayStatus.FUTURE
                        point?.earned == true -> WeekDayStatus.DISCIPLINED
                        dayTrades.isNotEmpty() -> WeekDayStatus.OVERRIDE
                        else -> WeekDayStatus.FUTURE
                    },
                    pnl = dayTrades
                        .filter { it.status != TradeStatus.OPEN }
                        .sumOf { it.pnl },
                    noOfTrades = dayTrades.size
                )
            }
        }
}

// extension to check if trade is in current week
fun TradeEntry.isCurrentWeek(): Boolean {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val monday = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
    val sunday = monday.plus(6, DateTimeUnit.DAY)
    val tradeDate = LocalDate.parse(date)
    return tradeDate in monday..sunday
}

sealed interface JournalHomeActions {
    object PreviousMonth : JournalHomeActions
    object NextMonth : JournalHomeActions
    object OnSetupJournal : JournalHomeActions
    data class OnDayClicked(val day: WeekDay) : JournalHomeActions
    object Refresh : JournalHomeActions
}

sealed interface JournalHomeNavigationEvent {
    data class ToSetup(
        val monthIndex: Int,
        val year: Int
    ) : JournalHomeNavigationEvent

    data class ToDayDetail(
        val date: String,
        val monthIndex: Int,
        val year: Int
    ) : JournalHomeNavigationEvent
}