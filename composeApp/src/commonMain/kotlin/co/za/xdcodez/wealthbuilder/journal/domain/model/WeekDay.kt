package co.za.xdcodez.wealthbuilder.journal.domain.model

data class WeekDay(
    val label: String = "",
    val date: String = "",
    val status: WeekDayStatus = WeekDayStatus.FUTURE,
    val pnl: Double = 0.0,
    val noOfTrades: Int = 0
)
