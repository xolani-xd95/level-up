package co.za.xdcodes.level_up.journal.data.model

data class DailyPointDocument(
    val date: String = "",
    val earned: Boolean = false,
    val reason: String = ""
)
