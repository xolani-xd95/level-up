package co.za.xdcodez.wealthbuilder.habits.domain

import co.za.xdcodez.wealthbuilder.habits.domain.model.HabitKey
import co.za.xdcodez.wealthbuilder.habits.domain.model.Quarter
import co.za.xdcodez.wealthbuilder.habits.domain.model.QuarterlyGoal
import co.za.xdcodez.wealthbuilder.habits.domain.model.WeeklyRollup

interface HabitsRepository {
    // ── Quarter Data ─────────────────────────────────────────────
    /**
     * Get quarter metadata (start/end dates, total days, current day number, etc.)
     */
    suspend fun getQuarter(quarter: String): Quarter?

    /**
     * Update the current day number in the quarter document
     */
    suspend fun updateQuarterDayNumber(quarter: String, dayNumber: Int): Result<Unit>

    /**
     * Increment the goal days completed counter in the quarter document
     */
    suspend fun incrementGoalDaysCompleted(quarter: String): Result<Unit>

    // ── Quarterly Goals ──────────────────────────────────────────
    suspend fun getQuarterlyGoals(quarter: String): List<QuarterlyGoal>
    suspend fun incrementGoalValue(quarter: String, goalId: String, incrementBy: Double = 1.0): Result<Unit>

    suspend fun updateGoalProgress(
        quarter: String,
        goalId: String,
        incrementValue: Double,
        lastUpdatedAt: String?
    ): Result<Unit>

    // ── Quarter Setup ────────────────────────────────────────────
    suspend fun initializeQuarter(quarter: String): Result<Unit>

    /**
     * Add missing goals to an existing quarter (e.g., adding Cardio goal)
     * Only adds goals that don't already exist
     */
    suspend fun addMissingGoalsToQuarter(quarter: String): Result<Unit>

    // ── Weekly Rollups ───────────────────────────────────────────
    /**
     * Get the weekly rollup for a specific week
     * @param quarter e.g., "2026-Q3"
     * @param weekStart Monday date as ISO string (e.g., "2026-07-07")
     */
    suspend fun getWeeklyRollup(quarter: String, weekStart: String): WeeklyRollup?

    /**
     * Update weekly rollup counters and daily log (called on each check-in)
     */
    suspend fun updateWeeklyRollup(
        quarter: String,
        weekStart: String,
        habitKey: HabitKey,
        date: String,  // Today's date
        completed: Boolean,
        focusBlocks: Int? = null
    ): Result<Unit>

    /**
     * Get current week's rollup based on today's date
     */
    suspend fun getCurrentWeekRollup(quarter: String): WeeklyRollup?

    /**
     * Update daily focus blocks count for a specific date
     * @param quarter e.g., "2026-Q3"
     * @param weekStart Monday date as ISO string (e.g., "2026-07-07")
     * @param date The date to update focus blocks for
     * @param blocksCompleted Number of focus blocks completed (0-3)
     */
    suspend fun updateDailyFocusBlocks(
        quarter: String,
        weekStart: String,
        date: String,
        blocksCompleted: Int
    ): Result<Unit>
}
