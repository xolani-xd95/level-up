package co.za.xdcodes.level_up.common

import co.za.xdcodes.level_up.dashboard.domain.dto.DailySummaryModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

expect fun formatCurrency(amount: Double): String

fun DailySummaryModel.dayLetter(): String {
    val date = LocalDate.parse(isoDate)
    return date.dayOfWeek.name.first().toString()
}

fun Int.toMonthName(): String =
    Month(this).name.lowercase().replaceFirstChar { it.uppercase() }