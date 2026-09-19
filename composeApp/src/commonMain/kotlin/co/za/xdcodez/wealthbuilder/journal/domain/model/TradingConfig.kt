package co.za.xdcodez.wealthbuilder.journal.domain.model

data class TradingConfig(
    val startingBalance: Double = 3000.0,
    val cycleStartDate: String = "", // e.g. "2026-07-27"
    val cycleEndDate: String = "",   // e.g. "2026-11-30"
    val maxTradesPerDay: Int = 4,
    val lossLimitPercent: Double = 0.03,
    val profitCeilingPercent: Double = 0.05,
    val sessionOneStart: String = "09:00",
    val sessionOneEnd: String = "12:00",
    val sessionTwoStart: String = "15:00",
    val sessionTwoEnd: String = "16:30",
    val isConfigured: Boolean = false,
)