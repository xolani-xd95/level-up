package co.za.xdcodes.level_up.common

import co.za.xdcodes.level_up.dashboard.domain.dto.DailySummaryModel
import kotlinx.datetime.LocalDate

expect fun formatCurrency(amount: Double): String

fun DailySummaryModel.dayLetter(): String {
    val date = LocalDate.parse(isoDate)
    return date.dayOfWeek.name.first().toString()
}

fun DailySummaryModel.dayNumber(): String {
    val date = LocalDate.parse(isoDate)
    return date.dayOfMonth.toString()
}
