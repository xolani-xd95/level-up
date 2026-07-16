package co.za.xdcodez.wealthbuilder.journal.data

import co.za.xdcodez.wealthbuilder.journal.data.model.MonthlyTradingTarget
import co.za.xdcodez.wealthbuilder.journal.domain.JournalRepository
import co.za.xdcodez.wealthbuilder.journal.domain.model.DailyPoint
import co.za.xdcodez.wealthbuilder.journal.domain.model.OverrideReason
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeDirection
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeEntry
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeStatus
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradingConfig
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FirebaseFirestore

class FirebaseTradingRepositoryImpl(
    private val firestore: FirebaseFirestore
) : JournalRepository {

    // ── Config ───────────────────────────────────────────────────

    override suspend fun getConfig(): TradingConfig? {
        return try {
            val snapshot = firestore
                .collection("tradingConfig")
                .document("config")
                .get()

            if (!snapshot.exists) return null

//            val doc = snapshot.data<TradingConfigDocument>()
//            doc.toDomain()
            TradingConfig(
                maxTradesPerDay = snapshot.get("maxTradesPerDay") ?: 4,
                lossLimitPercent = snapshot.get("lossLimitPercent") ?: 0.5,
                sessionOneStart = snapshot.get("sessionOneStart") ?: "09:00",
                sessionOneEnd = snapshot.get("sessionOneEnd") ?: "12:00",
                sessionTwoStart = snapshot.get("sessionTwoStart") ?: "15:00",
                sessionTwoEnd = snapshot.get("sessionTwoEnd") ?: "16:30",
                isConfigured = snapshot.get("isConfigured") ?: false
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveConfig(config: TradingConfig): Result<Unit> = try {
        firestore
            .collection("tradingConfig")
            .document("config")
            .set(config.toDocument())

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ── Monthly Target ───────────────────────────────────────────

    override suspend fun getMonthlyTarget(
        monthId: String
    ): MonthlyTradingTarget? {
        return try {
            val snapshot = firestore
                .collection("trading")
                .document("months")
                .collection(monthId)
                .document("target")
                .get()

            if (!snapshot.exists) return null

            MonthlyTradingTarget(
                monthId = monthId,
                monthlyTarget = snapshot.get("monthlyTarget") ?: 0.0
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveMonthlyTarget(
        target: MonthlyTradingTarget
    ): Result<Unit> = try {
        firestore
            .collection("trading")
            .document("months")
            .collection(target.monthId)
            .document("target")
            .set(mapOf("monthlyTarget" to target.monthlyTarget))
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ── Trades ───────────────────────────────────────────────────

    override suspend fun getTrades(month: Int, year: Int): List<TradeEntry> {
        return try {
            val startDate = "$year-${month.toString().padStart(2, '0')}-01"
            val nextMonth = if (month == 12) 1 else month + 1
            val nextYear = if (month == 12) year + 1 else year
            val endDate = "$nextYear-${nextMonth.toString().padStart(2, '0')}-01"

            val snapshot = firestore
                .collection("trades")
                .where { "date" greaterThanOrEqualTo startDate }
                .where { "date" lessThan endDate }
                .get()

            snapshot.documents.map { doc -> doc.toTradeEntry() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getTradesForDate(date: String): List<TradeEntry> {
        return try {
            val snapshot = firestore
                .collection("trades")
                .where { "date" equalTo date }
                .get()

            snapshot.documents.map { doc -> doc.toTradeEntry() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addTrade(trade: TradeEntry): Result<Unit> = try {
        firestore
            .collection("trades")
            .document(trade.id)
            .set(trade.toDocument())

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateTrade(trade: TradeEntry): Result<Unit> = try {
        firestore
            .collection("trades")
            .document(trade.id)
            .set(trade.toDocument())

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteTrade(tradeId: String): Result<Unit> = try {
        firestore
            .collection("trades")
            .document(tradeId)
            .delete()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ── Daily Points ─────────────────────────────────────────────

    override suspend fun getDailyPoints(month: Int, year: Int): List<DailyPoint> {
        return try {
            val startDate = "$year-${month.toString().padStart(2, '0')}-01"
            val nextMonth = if (month == 12) 1 else month + 1
            val nextYear = if (month == 12) year + 1 else year
            val endDate = "$nextYear-${nextMonth.toString().padStart(2, '0')}-01"

            val snapshot = firestore
                .collection("dailyPoints")
                .where { "date" greaterThanOrEqualTo startDate }
                .where { "date" lessThan endDate }
                .get()

            snapshot.documents.map { doc -> doc.toDailyPoint() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun saveDailyPoint(point: DailyPoint): Result<Unit> = try {
        firestore
            .collection("dailyPoints")
            .document(point.date)
            .set(point.toDocument())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
    // ── Mappers ──────────────────────────────────────────────────

    private fun DocumentSnapshot.toTradeEntry(): TradeEntry = TradeEntry(
        id = id,
        date = get("date") ?: "",
        direction = TradeDirection.valueOf(get("direction") ?: "LONG"),
        positionSize = get("positionSize") ?: 0.0,
        entryPrice = get("entryPrice") ?: 0.0,
        stopLoss = get("stopLoss") ?: 0.0,
        takeProfit = get("takeProfit") ?: 0.0,
        exitPrice = get("exitPrice"),
        pnl = get("pnl") ?: 0.0,
        status = TradeStatus.valueOf(get("status") ?: "OPEN"),
        notes = get("notes") ?: "",
        wasOverride = get("wasOverride") ?: false,
        overrideReason = get<String?>("overrideReason")
            ?.let { OverrideReason.valueOf(it) },
        openTime  = get("openTime")  ?: "",   // ← new
        closeTime = get("closeTime") ?: "",   // ← new
        session   = get("session")   ?: "MANUAL" // ← new
    )

    private fun TradeEntry.toDocument(): Map<String, Any?> = mapOf(
        "id" to id,
        "date" to date,
        "direction" to direction.name,
        "positionSize" to positionSize,
        "entryPrice" to entryPrice,
        "stopLoss" to stopLoss,
        "takeProfit" to takeProfit,
        "exitPrice" to exitPrice,
        "pnl" to pnl,
        "status" to status.name,
        "notes" to notes,
        "wasOverride" to wasOverride,
        "overrideReason" to overrideReason?.name
    )


    private fun TradingConfig.toDocument(): Map<String, Any> = mapOf(
        "maxTradesPerDay" to maxTradesPerDay,
        "lossLimitPercent" to lossLimitPercent,
        "sessionOneStart" to sessionOneStart,
        "sessionOneEnd" to sessionOneEnd,
        "sessionTwoStart" to sessionTwoStart,
        "sessionTwoEnd" to sessionTwoEnd,
        "isConfigured" to isConfigured
    )

    private fun DocumentSnapshot.toDailyPoint(): DailyPoint = DailyPoint(
        date = id,                          // document ID is the date
        earned = get("earned") ?: false,
        reason = get("reason") ?: ""
    )

    private fun DailyPoint.toDocument(): Map<String, Any> = mapOf(
        "date" to date,
        "earned" to earned,
        "reason" to reason
    )
}
