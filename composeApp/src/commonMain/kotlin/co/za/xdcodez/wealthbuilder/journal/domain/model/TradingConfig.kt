package co.za.xdcodez.wealthbuilder.journal.domain.model

data class TradingConfig(
    val monthlyTarget: Double = 0.0,
    val maxTradesPerDay: Int = 4,
    val lossLimitPercent: Double = 0.5,
    val sessionOneStart: String = "09:00",
    val sessionOneEnd: String = "12:00",
    val sessionTwoStart: String = "15:00",
    val sessionTwoEnd: String = "16:30",
    val isConfigured: Boolean = false
) {
    val dailyBaseTarget: Double
        get() = monthlyTarget / 20.0 // 20 trading days

    val lossLimitAmount: Double
        get() = dailyBaseTarget * lossLimitPercent
}
