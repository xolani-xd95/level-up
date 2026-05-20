package com.trading.journal.domain.model

import kotlinx.datetime.LocalDate

enum class TradeDirection { LONG, SHORT }
enum class AssetClass { STOCKS, FOREX, CRYPTO, FUTURES, OPTIONS, OTHER }
enum class TradeStatus { OPEN, CLOSED }

data class Trade(
    val id: String,
    val symbol: String,
    val direction: TradeDirection,
    val assetClass: AssetClass,
    val date: LocalDate,
    val entryPrice: Double,
    val exitPrice: Double? = null,
    val quantity: Double,
    val stopLoss: Double? = null,
    val takeProfit: Double? = null,
    val strategy: String = "",
    val notes: String = "",
    val tags: List<String> = emptyList(),
) {
    val status: TradeStatus get() = if (exitPrice != null) TradeStatus.CLOSED else TradeStatus.OPEN

    val pnl: Double?
        get() {
            val exit = exitPrice ?: return null
            val multiplier = if (direction == TradeDirection.LONG) 1.0 else -1.0
            return multiplier * (exit - entryPrice) * quantity
        }

    val isWin: Boolean? get() = pnl?.let { it >= 0 }

    val riskRewardRatio: Double?
        get() {
            val sl = stopLoss ?: return null
            val tp = takeProfit ?: return null
            val risk = kotlin.math.abs(entryPrice - sl)
            val reward = kotlin.math.abs(tp - entryPrice)
            return if (risk == 0.0) null else reward / risk
        }

    val pnlPercent: Double?
        get() = pnl?.let { (it / (entryPrice * quantity)) * 100 }
}
