package co.za.xdcodez.wealthbuilder.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import co.za.xdcodez.wealthbuilder.theme.Action
import co.za.xdcodez.wealthbuilder.theme.State
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

@Composable
fun budgetProgressColor(progress: Float): Color = when {
    progress > 1f     -> State.Error    // Over budget - error state
    progress == 1f    -> State.Success  // Exactly at target - success
    progress >= 0.75f -> State.Warning  // Approaching limit - warning
    else              -> Action.Primary // Under budget - primary action color
}

// Payday is on the 27th, but if it falls on a weekend, payment reflects on Saturday
const val PAYDAY = 27

/**
 * Calculate the actual payday start date considering weekends.
 * - If 27th is Mon-Fri: period starts on 27th
 * - If 27th is Saturday: period starts on 27th (payment reflects Saturday morning)
 * - If 27th is Sunday: period starts on 26th (Saturday - payment reflects Saturday morning)
 */
fun getActualPaydayStart(year: Int, month: Int): LocalDate {
    val nominalPayday = LocalDate(year, month, PAYDAY)
    return when (nominalPayday.dayOfWeek) {
        DayOfWeek.SUNDAY -> nominalPayday.minus(1, DateTimeUnit.DAY) // Move to Saturday
        else -> nominalPayday // Mon-Sat stays on 27th
    }
}

/**
 * Data class representing a budget period from payday to payday
 */
data class BudgetPeriod(
    val startDate: LocalDate,  // Actual payday (27th or 26th if 27th is Sunday)
    val endDate: LocalDate     // Day before next payday
) {
    val monthId: String
        get() = "${startDate.year}-${startDate.monthNumber.toString().padStart(2, '0')}-${startDate.dayOfMonth.toString().padStart(2, '0')}"

    val displayTitle: String
        get() {
            val startMonth = startDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
            val endMonth = endDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
            return if (startDate.year == endDate.year) {
                "$startMonth ${startDate.dayOfMonth} - $endMonth ${endDate.dayOfMonth}"
            } else {
                "$startMonth ${startDate.dayOfMonth}, ${startDate.year} - $endMonth ${endDate.dayOfMonth}, ${endDate.year}"
            }
        }
}

/**
 * Get the budget period that contains the given date.
 * Budget periods run from payday to the day before the next payday.
 * Payday is the 27th, but if it falls on Sunday, it's moved to Saturday (26th).
 */
fun getBudgetPeriodForDate(date: LocalDate): BudgetPeriod {
    // Calculate this month's actual payday
    val thisMonthPayday = getActualPaydayStart(date.year, date.monthNumber)

    return if (date >= thisMonthPayday) {
        // We're in a period that started this month
        val nextMonth = date.plus(1, DateTimeUnit.MONTH)
        val nextMonthPayday = getActualPaydayStart(nextMonth.year, nextMonth.monthNumber)
        val endDate = nextMonthPayday.minus(1, DateTimeUnit.DAY)
        BudgetPeriod(thisMonthPayday, endDate)
    } else {
        // We're in a period that started last month
        val lastMonth = date.minus(1, DateTimeUnit.MONTH)
        val lastMonthPayday = getActualPaydayStart(lastMonth.year, lastMonth.monthNumber)
        val endDate = thisMonthPayday.minus(1, DateTimeUnit.DAY)
        BudgetPeriod(lastMonthPayday, endDate)
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
 * Get the previous month's ID from a given monthId
 */
fun getPreviousMonthId(monthId: String): String {
    val currentPeriod = budgetPeriodFromMonthId(monthId)
    val previousPeriod = getPreviousBudgetPeriod(currentPeriod)
    return previousPeriod.monthId
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

fun getRemainingMessage(current: Double, target: Double): String {
    if (target <= 0.0) return ""
    val remaining = (target - current).coerceAtLeast(0.0)
    return if (current >= target) "Target hit — stop for today"
    else "${formatCurrency(remaining)} to go"
}