package co.za.xdcodez.wealthbuilder.journal.data.model

data class TradeDocument(
    val id: String = "",
    val date: String = "",
    val direction: String = "",
    val positionSize: Double = 0.0,
    val entryPrice: Double = 0.0,
    val stopLoss: Double = 0.0,
    val takeProfit: Double = 0.0,
    val exitPrice: Double? = null,
    val pnl: Double = 0.0,
    val status: String = "",
    val notes: String = "",
    val wasOverride: Boolean = false,
    val overrideReason: String? = null
)