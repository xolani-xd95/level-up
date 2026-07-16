package co.za.xdcodez.wealthbuilder.common
import platform.Foundation.*

actual fun formatCurrency(amount: Double): String {
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        locale = NSLocale(localeIdentifier = "en_ZA")
    }
    return formatter.stringFromNumber(NSNumber.numberWithDouble(amount)) ?: ""
}