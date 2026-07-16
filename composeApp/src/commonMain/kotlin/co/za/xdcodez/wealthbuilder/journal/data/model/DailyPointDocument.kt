package co.za.xdcodez.wealthbuilder.journal.data.model

data class DailyPointDocument(
    val date: String = "",
    val earned: Boolean = false,
    val reason: String = ""
)
