package co.za.xdcodez.wealthbuilder.habits.domain.model

/**
 * Represents a quarter period (e.g., 2026-Q3).
 * Stores metadata and progress tracking for the entire quarter.
 */
data class Quarter(
    val id: String,                    // e.g., "2026-Q3"
    val startDate: String,              // ISO date: "2026-07-01"
    val endDate: String,                // ISO date: "2026-09-30"
    val totalDays: Int,                 // Total days in quarter (usually 90-92)
    val currentDayNumber: Int = 1,      // Current day number in the quarter
    val goalDaysCompleted: Int = 0,     // Number of days where all habits were completed
    val title: String = "DISCIPLINED OPERATOR"
)
