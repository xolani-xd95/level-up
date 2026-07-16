package co.za.xdcodez.wealthbuilder.habits.domain.model

import kotlinx.datetime.LocalDate

data class HabitStreak(
    val habitKey: HabitKey,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastUpdated: LocalDate
)
