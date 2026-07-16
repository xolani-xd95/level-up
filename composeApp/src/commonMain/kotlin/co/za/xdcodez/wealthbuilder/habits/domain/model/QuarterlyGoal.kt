package co.za.xdcodez.wealthbuilder.habits.domain.model

import kotlinx.datetime.LocalDate

data class QuarterlyGoal(
    val id: String,
    val habitKey: HabitKey? = null,
    val title: String,
    val type: GoalType,
    val targetValue: Double? = null,
    val currentValue: Double? = null,
    val dueDate: LocalDate? = null,
    val isComplete: Boolean = false,
    val quarter: String,
    val lastUpdatedAt: String? = null,
    val weeklyTarget: Double? = null  // Target value per week (e.g., 4 gym sessions/week)
) {
    fun isDoneToday(today: LocalDate): Boolean {
        return lastUpdatedAt == today.toString()
    }
}
