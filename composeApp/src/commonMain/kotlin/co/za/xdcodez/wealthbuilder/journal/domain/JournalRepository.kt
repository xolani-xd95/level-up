package co.za.xdcodez.wealthbuilder.journal.domain

import co.za.xdcodez.wealthbuilder.journal.data.model.MonthlyTradingTarget
import co.za.xdcodez.wealthbuilder.journal.domain.model.AccountBalance
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeEntry
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradingConfig

interface JournalRepository {

    // ── Config ───────────────────────────────────────────────────
    suspend fun getConfig(): TradingConfig?
    suspend fun saveConfig(config: TradingConfig): Result<Unit>

    // ── Monthly Target ───────────────────────────────────────────
    suspend fun getMonthlyTarget(monthId: String): MonthlyTradingTarget?
    suspend fun saveMonthlyTarget(target: MonthlyTradingTarget): Result<Unit>

    // ── Trades ───────────────────────────────────────────────────
    suspend fun getTrades(month: Int, year: Int): List<TradeEntry>
    suspend fun getTradesForDate(date: String): List<TradeEntry>
    suspend fun getAllTradesInCycle(startDate: String, endDate: String): List<TradeEntry>
    suspend fun addTrade(trade: TradeEntry): Result<Unit>
    suspend fun updateTrade(trade: TradeEntry): Result<Unit>
    suspend fun deleteTrade(tradeId: String): Result<Unit>

    // ── Account Balance (EA synced) ─────────────────────────────
    suspend fun getAccountBalance(): AccountBalance?
}
