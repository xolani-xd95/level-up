package co.za.xdcodez.wealthbuilder.journal.domain.model

import kotlin.math.abs
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class TradeEntry @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val date: String = "",
    val direction: TradeDirection = TradeDirection.LONG,
    val positionSize: Double = 0.0,
    val entryPrice: Double = 0.0,
    val stopLoss: Double = 0.0,
    val takeProfit: Double = 0.0,
    val exitPrice: Double? = null,
    val pnl: Double = 0.0,
    val status: TradeStatus = TradeStatus.OPEN,
    val notes: String = "",
    val wasOverride: Boolean = false,
    val openTime: String = "",
    val closeTime: String = "",
    val session: String = "MANUAL",
    val overrideReason: OverrideReason? = null
) {
    val riskRewardRatio: Double
        get() {
            if (entryPrice == 0.0 || stopLoss == 0.0 || takeProfit == 0.0) return 0.0
            val risk = abs(entryPrice - stopLoss)
            val reward = abs(takeProfit - entryPrice)
            return if (risk == 0.0) 0.0 else reward / risk
        }

    val isOpen: Boolean get() = status == TradeStatus.OPEN
    val isClosed: Boolean get() = !isOpen
}
