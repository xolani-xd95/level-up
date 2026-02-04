package co.za.xdcodes.level_up.dashboard.domain

import co.za.xdcodes.level_up.dashboard.domain.dto.DashboardModel
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskModel

interface DashboardRepository {
    suspend fun getDashboardStreak(): DashboardModel
    suspend fun updateDashboardStreak(field: String, value: Int)
    suspend fun addTask(task: TaskModel): Result<Unit>
    suspend fun getTasks(date: String): List<TaskModel>
    suspend fun actionTask(task: TaskModel)
}