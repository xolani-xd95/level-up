package co.za.xdcodez.wealthbuilder.habits.domain.model

data class Habit(
    val key: HabitKey,
    val type: HabitType,
    val label: String,
    val isCore: Boolean,
    val icon: String
)
