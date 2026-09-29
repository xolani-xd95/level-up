package co.za.xdcodez.wealthbuilder.finance.domain.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Goal @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val name: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val startDate: String = "",
    val endDate: String = "",
    val linkedCategoryId: String? = null,
    val createdAt: String = ""
) {
    val progressPercentage: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f

    val remainingAmount: Double
        get() = (targetAmount - currentAmount).coerceAtLeast(0.0)

    val estimatedMonthlyContribution: Double
        get() {
            if (startDate.isEmpty() || endDate.isEmpty() || targetAmount <= 0.0) return 0.0

            return try {
                val start = LocalDate.parse(startDate)
                val end = LocalDate.parse(endDate)

                // Calculate months between dates
                val monthsDiff = ((end.year - start.year) * 12 + (end.monthNumber - start.monthNumber)).coerceAtLeast(1)

                remainingAmount / monthsDiff
            } catch (e: Exception) {
                0.0
            }
        }

    val isCompleted: Boolean
        get() = currentAmount >= targetAmount

    val daysRemaining: Int?
        get() {
            if (endDate.isEmpty()) return null
            return try {
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val end = LocalDate.parse(endDate)
                val diff = end.toEpochDays() - today.toEpochDays()
                diff.coerceAtLeast(0)
            } catch (e: Exception) {
                null
            }
        }
}
