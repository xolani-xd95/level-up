package co.za.xdcodez.wealthbuilder.common

import java.text.NumberFormat
import java.util.Locale

actual fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
    return formatter.format(amount)
}