package co.za.xdcodez.wealthbuilder.journal.presentation.details

import co.za.xdcodez.wealthbuilder.journal.data.model.MonthlyTradingTarget
import co.za.xdcodez.wealthbuilder.journal.domain.model.StopReason
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeEntry
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeStatus
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradingConfig
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

data class DayDetailState(
    val date: String = "",
    val config: TradingConfig? = null,
    val monthlyTarget: MonthlyTradingTarget? = null,
    val monthlyPnlSoFar: Double = 0.0,
    val trades: List<TradeEntry> = emptyList(),
    val isLoading: Boolean = false,
    val isToday: Boolean = false
) {
    val morningTrades: List<TradeEntry>
        get() = trades.filter { it.session == "MORNING" }

    val afternoonTrades: List<TradeEntry>
        get() = trades.filter { it.session == "AFTERNOON" }

    val morningPnl: Double
        get() = morningTrades.sumOf { it.pnl }

    val afternoonPnl: Double
        get() = afternoonTrades.sumOf { it.pnl }

    // ── Daily target (carry forward) ─────────────────────────────
    val dailyTarget: Double
        get() {
            val target = monthlyTarget?.monthlyTarget ?: 0.0
            val remaining = target - monthlyPnlSoFar
            val remainingDays = remainingTradingDays
            return if (remainingDays <= 0) 0.0
            else remaining / remainingDays
        }

    val lossLimitAmount: Double
        get() = dailyTarget * (config?.lossLimitPercent ?: 0.5)

    // ── Today's P&L ──────────────────────────────────────────────
    val todayPnl: Double
        get() = trades
            .filter { it.status != TradeStatus.OPEN }
            .sumOf { it.pnl }

    val todayProfit: Double
        get() = trades
            .filter { it.pnl > 0 }
            .sumOf { it.pnl }
    val todayLoss: Double
        get() = trades
            .filter { it.pnl < 0 }
            .sumOf { it.pnl }

    val tradesUsed: Int
        get() = trades.size

    val todayProgress: Float
        get() = if (dailyTarget <= 0.0) 0f
        else (todayPnl / dailyTarget).coerceIn(0.0, 1.0).toFloat()

    // ── Stop conditions ──────────────────────────────────────────
    val isTargetHit: Boolean
        get() = todayPnl >= dailyTarget && dailyTarget > 0.0

    val isLossLimitHit: Boolean
        get() = (todayLoss) >= lossLimitAmount && lossLimitAmount > 0.0

    val isMaxTradesHit: Boolean
        get() = tradesUsed >= (config?.maxTradesPerDay ?: 4)

    val isDoneForToday: Boolean
        get() = isTargetHit || isLossLimitHit || isMaxTradesHit

    val stopReason: StopReason?
        get() = when {
            isTargetHit     -> StopReason.TARGET_HIT
            isLossLimitHit  -> StopReason.LOSS_LIMIT_HIT
            isMaxTradesHit  -> StopReason.MAX_TRADES_HIT
            else            -> null
        }

    // ── Remaining trading days in month ──────────────────────────
    val remainingTradingDays: Int
        get() {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val lastDayOfMonth = LocalDate(today.year, today.monthNumber, 1)
                .plus(1, DateTimeUnit.MONTH)
                .minus(1, DateTimeUnit.DAY)
            var count = 0
            var current = today
            while (current <= lastDayOfMonth) {
                if (current.dayOfWeek != DayOfWeek.SATURDAY &&
                    current.dayOfWeek != DayOfWeek.SUNDAY
                ) count++
                current = current.plus(1, DateTimeUnit.DAY)
            }
            return count
        }
}

sealed interface DayDetailActions {
    object OnAddTrade : DayDetailActions
    data class OnTradeClicked(val trade: TradeEntry) : DayDetailActions
    object NavigateBack : DayDetailActions
}

sealed interface DayDetailNavigationEvent {
    object NavigateBack : DayDetailNavigationEvent
    object ToAddTrade : DayDetailNavigationEvent
    data class ToTradeDetail(val trade: TradeEntry) : DayDetailNavigationEvent
}