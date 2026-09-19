package co.za.xdcodez.wealthbuilder.journal.presentation.home

import co.za.xdcodez.wealthbuilder.journal.data.model.MonthlyTradingTarget
import co.za.xdcodez.wealthbuilder.journal.domain.model.AccountBalance
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeEntry
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeStatus
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradingConfig
import co.za.xdcodez.wealthbuilder.journal.domain.model.WeekDay
import co.za.xdcodez.wealthbuilder.journal.domain.model.WeekDayStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.math.min

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

    // All trades from cycle start to today (for capped compounding calculation)
    val allCycleTrades: List<TradeEntry> = emptyList(),

    // EA synced account balance from MT4/MT5
    val accountBalance: AccountBalance? = null,

    val isLoading: Boolean = false,
    val error: String? = null
) {

    private val today: LocalDate
        get() = Clock.System.todayIn(TimeZone.currentSystemDefault())

    private val todayStr: String
        get() = today.toString()

    /**
     * Calculates today's starting balance using capped compounding:
     * - For each past day: add min(actualProfit, target) to running balance
     * - Losses carry forward as-is (no cap on downside)
     * - Gains are capped at daily target (no reward for overtrading)
     */
    val dayStart: Double
        get() {
            val cycleStart = config?.cycleStartDate?.takeIf { it.isNotBlank() }?.let {
                try {
                    LocalDate.parse(it)
                } catch (_: Exception) {
                    null
                }
            } ?: return 0.0
            val startingBalance = config.startingBalance
            val profitPercent = config.profitCeilingPercent

            if (today <= cycleStart) return startingBalance

            // Group all cycle trades by date
            val tradesByDate = allCycleTrades
                .filter { it.status != TradeStatus.OPEN }
                .groupBy { it.date }

            var balance = startingBalance
            var currentDate = cycleStart

            // Walk through each day from cycle start to yesterday
            while (currentDate < today) {
                // Skip weekends
                if (currentDate.dayOfWeek != DayOfWeek.SATURDAY &&
                    currentDate.dayOfWeek != DayOfWeek.SUNDAY
                ) {
                    val dayTarget = balance * profitPercent
                    val dayProfit = tradesByDate[currentDate.toString()]
                        ?.sumOf { it.pnl } ?: 0.0

                    // Capped compounding: gains capped at target, losses pass through
                    val cappedGain = if (dayProfit > 0) {
                        min(dayProfit, dayTarget)
                    } else {
                        dayProfit // losses are not capped
                    }

                    balance += cappedGain
                    // Ensure balance doesn't go below 0
                    balance = balance.coerceAtLeast(0.0)
                }
                currentDate = currentDate.plus(1, DateTimeUnit.DAY)
            }

            return balance
        }

    val target: Double
        get() = dayStart * (config?.profitCeilingPercent ?: 0.0)

    val todaysProfit: Double
        get() = allCycleTrades
            .filter { it.date == todayStr && it.status != TradeStatus.OPEN }
            .sumOf { it.pnl }

    /**
     * Live balance = today's start + today's profit
     */
    val liveBalance: Double
        get() = dayStart + todaysProfit

    /**
     * Number of trading days left in the cycle (excluding weekends)
     */
    val tradingDaysLeft: Int
        get() {
            val cycleEnd = config?.cycleEndDate?.takeIf { it.isNotBlank() }?.let {
                try {
                    LocalDate.parse(it)
                } catch (_: Exception) {
                    null
                }
            } ?: return 0

            if (today >= cycleEnd) return 0

            var count = 0
            var currentDate = today

            while (currentDate <= cycleEnd) {
                if (currentDate.dayOfWeek != DayOfWeek.SATURDAY &&
                    currentDate.dayOfWeek != DayOfWeek.SUNDAY
                ) {
                    count++
                }
                currentDate = currentDate.plus(1, DateTimeUnit.DAY)
            }
            return count
        }

    /**
     * Total trading days in the cycle (from cycle start to cycle end, excluding weekends)
     */
    val totalCycleTradingDays: Int
        get() {
            val cycleStart = config?.cycleStartDate?.takeIf { it.isNotBlank() }?.let {
                try {
                    LocalDate.parse(it)
                } catch (_: Exception) {
                    null
                }
            } ?: return 0

            val cycleEnd = config?.cycleEndDate?.takeIf { it.isNotBlank() }?.let {
                try {
                    LocalDate.parse(it)
                } catch (_: Exception) {
                    null
                }
            } ?: return 0

            var count = 0
            var currentDate = cycleStart

            while (currentDate <= cycleEnd) {
                if (currentDate.dayOfWeek != DayOfWeek.SATURDAY &&
                    currentDate.dayOfWeek != DayOfWeek.SUNDAY
                ) {
                    count++
                }
                currentDate = currentDate.plus(1, DateTimeUnit.DAY)
            }
            return count
        }

    /**
     * Estimated goal balance if hitting 5% target every trading day in the full cycle
     * Uses compound growth: startingBalance * (1 + profitPercent)^totalCycleDays
     */
    val estimatedGoalBalance: Double
        get() {
            val profitPercent = config?.profitCeilingPercent ?: 0.0
            val startingBalance = config?.startingBalance ?: 0.0
            val totalDays = totalCycleTradingDays

            if (totalDays == 0 || profitPercent == 0.0) return startingBalance

            // Compound: balance * (1 + rate)^days
            var balance = startingBalance
            repeat(totalDays) {
                balance *= (1 + profitPercent)
            }
            return balance
        }

    // ── Monthly Performance ─────────────────────────────────────────────
    val winRate: Float
        get() {
            val closed = allMonthTrades.filter { it.status != TradeStatus.OPEN }
            if (closed.isEmpty()) return 0f
            return (closed.count { it.status == TradeStatus.WIN }.toFloat() / closed.size) * 100
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
            .filter { it < 0.0 }
            .minOrNull() ?: 0.0


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
                val dayTrades = allMonthTrades.filter { it.date == dateStr }
                WeekDay(
                    label = date.dayOfWeek.name.take(3).uppercase(),
                    date = dateStr,
                    status = when {
                        date == today -> WeekDayStatus.TODAY
                        date > today -> WeekDayStatus.FUTURE
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

sealed interface JournalHomeActions {
    object PreviousMonth : JournalHomeActions
    object NextMonth : JournalHomeActions
    data class OnDayClicked(val day: WeekDay) : JournalHomeActions
    object Refresh : JournalHomeActions
}

sealed interface JournalHomeNavigationEvent {
    data class ToDayDetail(
        val date: String,
        val monthIndex: Int,
        val year: Int
    ) : JournalHomeNavigationEvent
}