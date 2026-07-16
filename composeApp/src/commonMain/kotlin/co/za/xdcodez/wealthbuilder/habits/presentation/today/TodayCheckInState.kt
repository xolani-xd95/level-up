package co.za.xdcodez.wealthbuilder.habits.presentation.today

import co.za.xdcodez.wealthbuilder.habits.domain.model.HabitKey
import co.za.xdcodez.wealthbuilder.habits.domain.model.QuarterlyGoal
import co.za.xdcodez.wealthbuilder.habits.domain.model.WeeklyRollup
import kotlinx.datetime.LocalDate

data class TodayCheckInState(
    val currentDate: LocalDate? = null,
    val quarterDayNumber: Int = 1,
    val totalQuarterDays: Int = 92,
    val completedDays: Int = 1,
    val quarterlyGoals: List<QuarterlyGoal> = emptyList(),
    val isLoading: Boolean = false,
    val showSlipSheet: Boolean = false,
    // Daily progress tracking (temporary counters for today)
    val focusBlocksCompletedToday: Int = 0,  // 0-3 blocks completed today, resets daily

    // Weekly tracking
    val currentWeekRollup: WeeklyRollup? = null,
    val currentWeekStart: LocalDate? = null,  // This Monday
    val currentWeekEnd: LocalDate? = null     // This Sunday
) {
    /**
     * Helper to get a goal by its habitKey
     */
    fun getGoalByHabit(habitKey: HabitKey): QuarterlyGoal? {
        return quarterlyGoals.firstOrNull { it.habitKey == habitKey }
    }

    /**
     * Get weekly progress for a habit (current/target)
     */
    fun getWeeklyProgress(habitKey: HabitKey): Pair<Int, Int>? {
        val goal = getGoalByHabit(habitKey) ?: return null
        val target = goal.weeklyTarget?.toInt() ?: return null
        val current = when (habitKey) {
            HabitKey.IMPULSE_CONTROL -> currentWeekRollup?.impulseControlDays ?: 0
            HabitKey.GYM -> currentWeekRollup?.gymDays ?: 0
            HabitKey.FOCUS -> currentWeekRollup?.focusDays ?: 0
            HabitKey.CARDIO -> currentWeekRollup?.cardioDays ?: 0
        }
        return current to target
    }
}

sealed class TodayCheckInAction {
    object OnCardioTap : TodayCheckInAction()
    object OnGymTap : TodayCheckInAction()
    object OnImpulseTap : TodayCheckInAction()
    data class OnFocusBlockTap(val blockNumber: Int) : TodayCheckInAction()
}