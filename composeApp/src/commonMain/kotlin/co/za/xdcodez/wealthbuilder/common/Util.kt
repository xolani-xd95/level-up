package co.za.xdcodez.wealthbuilder.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

@Composable
fun budgetProgressColor(progress: Float): Color = when {
    progress > 1f     -> Color(0xFFFF2400)
    progress == 1f    -> Color(0xFF00C853)
    progress >= 0.75f -> Color(0xFFFFA500)
    else              -> MaterialTheme.colorScheme.primary
}

// Payday is always on the 27th
const val PAYDAY = 27

/**
 * Data class representing a budget period from payday to payday
 */
data class BudgetPeriod(
    val startDate: LocalDate,  // 27th of start month
    val endDate: LocalDate     // 26th of end month (day before next payday)
) {
    val monthId: String
        get() = "${startDate.year}-${startDate.monthNumber.toString().padStart(2, '0')}-$PAYDAY"

    val displayTitle: String
        get() {
            val startMonth = startDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
            val endMonth = endDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
            return if (startDate.year == endDate.year) {
                "$startMonth $PAYDAY - $endMonth ${endDate.dayOfMonth}"
            } else {
                "$startMonth $PAYDAY, ${startDate.year} - $endMonth ${endDate.dayOfMonth}, ${endDate.year}"
            }
        }
}

/**
 * Get the budget period that contains the given date.
 * Budget periods run from the 27th of one month to the 26th of the next.
 */
fun getBudgetPeriodForDate(date: LocalDate): BudgetPeriod {
    return if (date.dayOfMonth >= PAYDAY) {
        // We're in a period that started this month
        val startDate = LocalDate(date.year, date.monthNumber, PAYDAY)
        val nextMonth = startDate.plus(1, DateTimeUnit.MONTH)
        val endDate = LocalDate(nextMonth.year, nextMonth.monthNumber, PAYDAY - 1)
        BudgetPeriod(startDate, endDate)
    } else {
        // We're in a period that started last month
        val lastMonth = date.minus(1, DateTimeUnit.MONTH)
        val startDate = LocalDate(lastMonth.year, lastMonth.monthNumber, PAYDAY)
        val endDate = LocalDate(date.year, date.monthNumber, PAYDAY - 1)
        BudgetPeriod(startDate, endDate)
    }
}

/**
 * Get the current budget period based on today's date
 */
fun getCurrentBudgetPeriod(): BudgetPeriod {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    return getBudgetPeriodForDate(today)
}

/**
 * Get the next budget period after the given period
 */
fun getNextBudgetPeriod(currentPeriod: BudgetPeriod): BudgetPeriod {
    val nextStart = currentPeriod.startDate.plus(1, DateTimeUnit.MONTH)
    return getBudgetPeriodForDate(nextStart)
}

/**
 * Get the previous budget period before the given period
 */
fun getPreviousBudgetPeriod(currentPeriod: BudgetPeriod): BudgetPeriod {
    val prevStart = currentPeriod.startDate.minus(1, DateTimeUnit.MONTH)
    return getBudgetPeriodForDate(prevStart)
}

/**
 * Create a budget period from a monthId string (format: "YYYY-MM-27")
 */
fun budgetPeriodFromMonthId(monthId: String): BudgetPeriod {
    val startDate = LocalDate.parse(monthId)
    return getBudgetPeriodForDate(startDate)
}

/**
 * @deprecated Use BudgetPeriod.monthId instead
 */
fun monthId(monthIndex: Int, year: Int): String {
    return "$year-${monthIndex.toString().padStart(2, '0')}-$PAYDAY"
}

fun String.toFormattedDate(): String {
    return try {
        val date = LocalDate.parse(this)
        val day = date.dayOfMonth
        val suffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else          -> "th"
        }
        val month = date.month.name
            .lowercase()
            .replaceFirstChar { it.uppercase() }
        "$month ${day}${suffix}"
    } catch (e: Exception) {
        this
    }
}