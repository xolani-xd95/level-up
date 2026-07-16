package co.za.xdcodez.wealthbuilder.habits.domain.model

import kotlinx.datetime.LocalDate

sealed class Badge(
    open val id: String,
    open val label: String,
    open val icon: String
) {
    data class SmokeFreeMilestone(
        val days: Int,
        override val id: String = "smoke_free_$days",
        override val label: String = "$days Days Smoke-Free",
        override val icon: String = "🚭"
    ) : Badge(id, label, icon)

    data class PerfectFocusWeek(
        val week: LocalDate,
        override val id: String = "perfect_focus_$week",
        override val label: String = "Perfect Focus Week",
        override val icon: String = "🔥"
    ) : Badge(id, label, icon)

    data class GymStreak(
        val days: Int,
        override val id: String = "gym_streak_$days",
        override val label: String = "$days-Day Gym Streak",
        override val icon: String = "🏋️"
    ) : Badge(id, label, icon)
}
