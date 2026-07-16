package co.za.xdcodez.wealthbuilder.habits.domain.model

import kotlinx.datetime.LocalDate

data class WeeklyRollup(
    val weekStart: LocalDate,        // Monday date
    val weekEnd: LocalDate,          // Sunday date
    val weekNumber: Int,             // Week in quarter (1-13)

    // Per-habit counters
    val impulseControlDays: Int,     // 0-7
    val gymDays: Int,                // 0-7
    val cardioDays: Int,             // 0-7
    val focusDays: Int,              // 0-7

    // Daily focus blocks tracking (date string -> blocks completed 0-3)
    val dailyFocusBlocks: Map<String, Int> = emptyMap()
) {
    /**
     * Get focus blocks completed for a specific date
     */
    fun getFocusBlocksForDate(date: LocalDate): Int {
        return dailyFocusBlocks[date.toString()] ?: 0
    }
}
