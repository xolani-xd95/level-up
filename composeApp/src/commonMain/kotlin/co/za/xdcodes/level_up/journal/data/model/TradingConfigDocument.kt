package co.za.xdcodes.level_up.journal.data.model

data class TradingConfigDocument(
    val maxTradesPerDay: Int = 4,
    val lossLimitPercent: Double = 0.5,
    val sessionOneStart: String = "09:00",
    val sessionOneEnd: String = "12:00",
    val sessionTwoStart: String = "15:00",
    val sessionTwoEnd: String = "16:30",
    val isConfigured: Boolean = false
)

data class MonthlyTradingTarget(
    val monthId: String = "",
    val monthlyTarget: Double = 0.0,
)
