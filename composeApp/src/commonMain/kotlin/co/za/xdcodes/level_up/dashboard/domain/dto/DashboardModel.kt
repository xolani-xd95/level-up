package co.za.xdcodes.level_up.dashboard.domain.dto

data class DashboardModel(
    val goalDaysCompleted: Int = 0,
    val goalDays: Int = 90,
    val workoutStreak: Int = 0,
    val runningStreak: Int = 0,
    val tradingDiscipline: Int = 0,
    val lastCheckInDate: String = "",
)