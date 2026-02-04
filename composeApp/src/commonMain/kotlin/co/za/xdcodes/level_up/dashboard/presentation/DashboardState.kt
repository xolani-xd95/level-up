package co.za.xdcodes.level_up.dashboard.presentation

import co.za.xdcodes.level_up.dashboard.domain.dto.CategoryMedalTier
import co.za.xdcodes.level_up.dashboard.domain.dto.ConsistencyTier
import co.za.xdcodes.level_up.dashboard.domain.dto.DailySummaryModel
import co.za.xdcodes.level_up.dashboard.domain.dto.DashboardModel
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskModel

data class DashboardState(
    val showAddTaskBottomSheet: Boolean = false,
    val dashboardStreaks: DashboardModel? = DashboardModel(),
    val badgeStreaks: Pair<ConsistencyTier?, ConsistencyTier?>? = null,
    val workoutStreaks: Pair<CategoryMedalTier?, CategoryMedalTier?>? = null,
    val tradingStreaks: Pair<CategoryMedalTier?, CategoryMedalTier?>? = null,
    val runningStreaks: Pair<CategoryMedalTier?, CategoryMedalTier?>? = null,
    val taskForToday: List<TaskModel> = emptyList(),
    val weekDays: List<DailySummaryModel> = emptyList()
)

sealed interface DashboardActions {
    data class OnShowAddTaskBottomSheet(val show: Boolean): DashboardActions
    data class OnTaskAdded(val task: TaskModel): DashboardActions
    data class OnStartTask(val id: String): DashboardActions
    data class OnDeleteTask(val id: String): DashboardActions
    data class OnCompleteTask(val id: String): DashboardActions
}