package co.za.xdcodes.level_up.journal.domain.model

data class DailyPoint(
    val date: String = "",
    val earned: Boolean = false,
    val reason: String = ""
)
