package co.za.xdcodez.wealthbuilder.journal.domain.model

data class DailyPoint(
    val date: String = "",
    val earned: Boolean = false,
    val reason: String = ""
)
