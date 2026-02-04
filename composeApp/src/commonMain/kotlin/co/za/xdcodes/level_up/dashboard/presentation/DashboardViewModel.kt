package co.za.xdcodes.level_up.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodes.level_up.dashboard.domain.DashboardRepository
import co.za.xdcodes.level_up.dashboard.domain.dto.CategoryMedalTier
import co.za.xdcodes.level_up.dashboard.domain.dto.ConsistencyTier
import co.za.xdcodes.level_up.dashboard.domain.dto.DailySummaryModel
import co.za.xdcodes.level_up.dashboard.domain.dto.DashboardModel
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskCategory
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskModel
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskState
import co.za.xdcodes.level_up.dashboard.domain.dto.nonSmokingMedals
import co.za.xdcodes.level_up.dashboard.domain.dto.runningMedals
import co.za.xdcodes.level_up.dashboard.domain.dto.workoutMedals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class DashboardViewModel(
    private val repository: DashboardRepository
) : ViewModel() {
    private val _state = MutableStateFlow(
        DashboardState(
            weekDays = listOf(
                DailySummaryModel(
                    "M",
                    true,
                    false,
                    false
                ),
                DailySummaryModel(
                    "M",
                    true,
                    false,
                    false
                ),
                DailySummaryModel(
                    "M",
                    false,
                    false,
                    false
                ),
                DailySummaryModel(
                    "M",
                    false,
                    false,
                    false
                ),
                DailySummaryModel(
                    "M",
                    false,
                    false,
                    false
                ),
            )
        )
    )
    val state = _state.asStateFlow()

    init {
        getDashboardStreaks()
        getTaskForToday()

    }

    fun onAction(action: DashboardActions) {
        when (action) {
            is DashboardActions.OnShowAddTaskBottomSheet -> {
                _state.update {
                    it.copy(
                        showAddTaskBottomSheet = action.show
                    )
                }
            }

            is DashboardActions.OnTaskAdded -> {
                viewModelScope.launch {
                    val result = repository.addTask(action.task)
                    if (result.isSuccess) {
                        getTaskForToday()
                        _state.update {
                            it.copy(
                                showAddTaskBottomSheet = false
                            )
                        }
                    }
                }
            }

            is DashboardActions.OnStartTask -> updateTask(action.id, TaskState.STARTED)
            is DashboardActions.OnCompleteTask -> updateTask(action.id, TaskState.DONE)
            is DashboardActions.OnDeleteTask -> {}
        }
    }

    private fun updateTask(taskId: String, taskState: TaskState) {
        val currentTasks = state.value.taskForToday

        val existingTask = currentTasks.firstOrNull { it.id == taskId } ?: return

        val isTransitionToDone =
            existingTask.state != TaskState.DONE && taskState == TaskState.DONE

        val updatedTask = existingTask.copy(state = taskState)

        _state.update { currentState ->
            currentState.copy(
                taskForToday = currentTasks.map {
                    if (it.id == taskId) updatedTask else it
                }
            )
        }

        if (isTransitionToDone) {
            handleStreakUpdate(updatedTask)
        }

        viewModelScope.launch {
            repository.actionTask(updatedTask)
        }
    }

    private fun checkAndUpdateOverallProgress(today: String) {
        val dashboard = state.value.dashboardStreaks ?: return

        val total = state.value.taskForToday.size
        if (total == 0) return

        val completed = state.value.taskForToday.count { it.state == TaskState.DONE }
        val ratio = completed.toFloat() / total.toFloat()

        if (ratio == 1f) {
            viewModelScope.launch {
                repository.updateDashboardStreak(
                    "goalDaysCompleted",
                    dashboard.goalDaysCompleted + 1
                )
            }
            _state.update { currentState ->
                currentState.copy(
                    dashboardStreaks = dashboard.copy(
                        goalDaysCompleted = dashboard.goalDaysCompleted + 1,
                    )
                )
            }
        }
    }

    private fun handleStreakUpdate(task: TaskModel) {
        _state.update { currentState ->
            val dashboard = currentState.dashboardStreaks
                ?: return@update currentState.copy(
                    dashboardStreaks = DashboardModel()
                )

            val updatedDashboard = when (task.category) {

                TaskCategory.WORKOUT -> {
                    viewModelScope.launch {
                        repository.updateDashboardStreak(
                            "workoutStreak",
                            dashboard.workoutStreak + 1
                        )
                    }
                    dashboard.copy(workoutStreak = dashboard.workoutStreak + 1)
                }

                TaskCategory.TRADING -> {
                    viewModelScope.launch {
                        repository.updateDashboardStreak(
                            "tradingDiscipline",
                            dashboard.tradingDiscipline + 1
                        )
                    }
                    dashboard.copy(tradingDiscipline = dashboard.tradingDiscipline + 1)
                }

                TaskCategory.RUNNING -> {
                    viewModelScope.launch {
                        repository.updateDashboardStreak(
                            "runningStreaks",
                            dashboard.runningStreak + 1
                        )
                    }
                    dashboard.copy(runningStreak = dashboard.runningStreak + 1)
                }

                else -> dashboard
            }

            currentState.copy(dashboardStreaks = updatedDashboard)
        }

        checkAndUpdateOverallProgress("")
    }

    private fun getDashboardStreaks() {
        viewModelScope.launch {
            val dashboardStreaks = repository.getDashboardStreak()
            val tiers = ConsistencyTier.entries.toTypedArray()

            val current = tiers
                .filter { dashboardStreaks.goalDaysCompleted >= it.daysRequired }
                .maxByOrNull { it.daysRequired }

            val next = tiers
                .firstOrNull { it.daysRequired > dashboardStreaks.goalDaysCompleted }

            _state.update {
                it.copy(
                    workoutStreaks = resolveCategoryMedal(
                        progressCount = dashboardStreaks.workoutStreak,
                        workoutMedals
                    ),
                    runningStreaks = resolveCategoryMedal(
                        progressCount = dashboardStreaks.runningStreak,
                        runningMedals
                    ),
                    tradingStreaks = resolveCategoryMedal(
                        progressCount = dashboardStreaks.tradingDiscipline,
                        nonSmokingMedals
                    ),

                    badgeStreaks = current to next,
                    dashboardStreaks = dashboardStreaks
                )
            }

        }
    }

    fun resolveCategoryMedal(
        progressCount: Int,
        medals: List<CategoryMedalTier>
    ): Pair<CategoryMedalTier?, CategoryMedalTier?> {

        val earned = medals
            .filter { progressCount >= it.requiredCount }
            .maxByOrNull { it.requiredCount }

        val next = medals
            .filter { progressCount < it.requiredCount }
            .minByOrNull { it.requiredCount }

        return earned to next
    }

    fun getTaskForToday() {
        viewModelScope.launch {
            val timeZone = TimeZone.currentSystemDefault()
            val currentDate = Clock.System.todayIn(timeZone).toString()

            val listOfTasks = repository.getTasks(currentDate)
            _state.update {
                it.copy(
                    taskForToday = listOfTasks
                )
            }
        }
    }
}