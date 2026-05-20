package co.za.xdcodes.level_up.journal.domain

import co.za.xdcodes.level_up.journal.data.model.MonthlyTradingTarget
import co.za.xdcodes.level_up.journal.domain.model.DailyPoint
import co.za.xdcodes.level_up.journal.domain.model.TradeEntry
import co.za.xdcodes.level_up.journal.domain.model.TradingConfig

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
    suspend fun addTrade(trade: TradeEntry): Result<Unit>
    suspend fun updateTrade(trade: TradeEntry): Result<Unit>
    suspend fun deleteTrade(tradeId: String): Result<Unit>

    // ── Daily Points ─────────────────────────────────────────────
    suspend fun getDailyPoints(month: Int, year: Int): List<DailyPoint>
    suspend fun saveDailyPoint(point: DailyPoint): Result<Unit>
}
