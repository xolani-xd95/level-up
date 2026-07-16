package co.za.xdcodez.wealthbuilder.common

import kotlinx.datetime.Month

expect fun formatCurrency(amount: Double): String

fun Int.toMonthName(): String =
    Month(this).name.lowercase().replaceFirstChar { it.uppercase() }