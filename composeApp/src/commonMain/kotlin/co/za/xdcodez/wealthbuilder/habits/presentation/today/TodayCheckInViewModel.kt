package co.za.xdcodez.wealthbuilder.habits.presentation.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodez.wealthbuilder.habits.domain.HabitsRepository
import co.za.xdcodez.wealthbuilder.habits.domain.model.HabitKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class TodayCheckInViewModel(
    private val repository: HabitsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TodayCheckInState())
    val state = _state.asStateFlow()

    fun initializeAndLoadQuarter(quarter: String) {
        viewModelScope.launch {
            loadQuarterData(quarter)
        }
    }

    private suspend fun loadQuarterData(quarter: String) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        // Calculate current week boundaries (Monday-Sunday)
        val weekStart = getWeekStart(today)
        val weekEnd = LocalDate(weekStart.year, weekStart.monthNumber, weekStart.dayOfMonth + 6)

        // Load quarter metadata from Firebase
        var quarterData = repository.getQuarter(quarter)

        // Initialize quarter if it doesn't exist
        if (quarterData == null) {
            println("🟢 Quarter $quarter not found. Initializing...")
            val initResult = repository.initializeQuarter(quarter)
            if (initResult.isSuccess) {
                println("🟢 Quarter $quarter initialized successfully")
                quarterData = repository.getQuarter(quarter)
            } else {
                println("🔴 Failed to initialize quarter $quarter: ${initResult.exceptionOrNull()?.message}")
            }
        }

        // Load goals
        val goals = repository.getQuarterlyGoals(quarter)

        // Load current week rollup
        val weekRollup = repository.getCurrentWeekRollup(quarter)

        // Calculate current day number from today's date
        val quarterDayNumber = if (quarterData != null) {
            val startDate = LocalDate.parse(quarterData.startDate)
            val daysSinceStart = (today.toEpochDays() - startDate.toEpochDays()).toInt()
            (daysSinceStart + 1).coerceAtLeast(1)
        } else {
            // Fallback: calculate manually if quarter data doesn't exist
            val (year, quarterNum) = quarter.split("-Q").let {
                it[0].toInt() to it[1].toInt()
            }
            val quarterStartMonth = (quarterNum - 1) * 3 + 1
            val quarterStart = LocalDate(year, quarterStartMonth, 1)
            val daysSinceStart = (today.toEpochDays() - quarterStart.toEpochDays()).toInt()
            daysSinceStart + 1
        }

        // Update quarter day number in Firebase if needed
        if (quarterData != null && quarterData.currentDayNumber != quarterDayNumber) {
            repository.updateQuarterDayNumber(quarter, quarterDayNumber)
        }

        // Load focus blocks from Firebase for today
        // If it's a new day, get from weekly rollup; otherwise keep current state
        val currentFocusBlocks = if (_state.value.currentDate == today) {
            _state.value.focusBlocksCompletedToday
        } else {
            // New day or initial load - get from Firebase
            weekRollup?.getFocusBlocksForDate(today) ?: 0
        }

        _state.update {
            it.copy(
                currentDate = today,
                quarterlyGoals = goals,
                completedDays = quarterData?.goalDaysCompleted ?: 0,
                quarterDayNumber = quarterDayNumber,
                totalQuarterDays = quarterData?.totalDays ?: 92,
                focusBlocksCompletedToday = currentFocusBlocks,

                // Weekly tracking
                currentWeekRollup = weekRollup,
                currentWeekStart = weekStart,
                currentWeekEnd = weekEnd
            )
        }
    }

    private fun getWeekStart(date: LocalDate): LocalDate {
        val dayOfWeek = date.dayOfWeek.ordinal
        return LocalDate(date.year, date.monthNumber, date.dayOfMonth - dayOfWeek)
    }

    fun onAction(action: TodayCheckInAction) {
        when (action) {
            TodayCheckInAction.OnGymTap -> {
                viewModelScope.launch {
                    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                    val gymGoal = _state.value.getGoalByHabit(co.za.xdcodez.wealthbuilder.habits.domain.model.HabitKey.GYM)

                    // Check if already done today
                    if (gymGoal?.isDoneToday(today) == true) return@launch

                    val quarter = getCurrentQuarter()
                    val weekStart = getWeekStart(today)

                    // Update quarterly goal
                    val result = repository.updateGoalProgress(
                        quarter = quarter,
                        goalId = gymGoal?.id ?: return@launch,
                        incrementValue = 1.0,
                        lastUpdatedAt = today.toString()
                    )

                    // Update weekly rollup
                    if (result.isSuccess) {
                        repository.updateWeeklyRollup(
                            quarter = quarter,
                            weekStart = weekStart.toString(),
                            habitKey = HabitKey.GYM,
                            date = today.toString(),
                            completed = true
                        )
                        loadQuarterData(quarter)
                    }
                }
            }
            TodayCheckInAction.OnCardioTap -> {
                viewModelScope.launch {
                    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                    val cardioGoal = _state.value.getGoalByHabit(HabitKey.CARDIO)

                    // Check if already done today
                    if (cardioGoal?.isDoneToday(today) == true) return@launch

                    val quarter = getCurrentQuarter()
                    val weekStart = getWeekStart(today)

                    // Update quarterly goal
                    val result = repository.updateGoalProgress(
                        quarter = quarter,
                        goalId = cardioGoal?.id ?: return@launch,
                        incrementValue = 1.0,
                        lastUpdatedAt = today.toString()
                    )

                    // Update weekly rollup
                    if (result.isSuccess) {
                        repository.updateWeeklyRollup(
                            quarter = quarter,
                            weekStart = weekStart.toString(),
                            habitKey = HabitKey.CARDIO,
                            date = today.toString(),
                            completed = true
                        )
                        loadQuarterData(quarter)
                    }
                }
            }

            is TodayCheckInAction.OnFocusBlockTap -> {
                viewModelScope.launch {
                    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                    val focusGoal = _state.value.getGoalByHabit(co.za.xdcodez.wealthbuilder.habits.domain.model.HabitKey.FOCUS)
                    val currentBlocks = _state.value.focusBlocksCompletedToday

                    // Max 3 blocks
                    if (currentBlocks >= 3) return@launch

                    val newBlockCount = currentBlocks + 1
                    val quarter = getCurrentQuarter()
                    val weekStart = getWeekStart(today)

                    // Update local state immediately
                    _state.update { it.copy(focusBlocksCompletedToday = newBlockCount) }

                    // Save daily focus blocks to Firebase
                    repository.updateDailyFocusBlocks(
                        quarter = quarter,
                        weekStart = weekStart.toString(),
                        date = today.toString(),
                        blocksCompleted = newBlockCount
                    )

                    // When all 3 blocks completed, increment focusDays and update quarterly goal
                    if (newBlockCount == 3) {
                        // Increment weekly focusDays counter
                        repository.updateWeeklyRollup(
                            quarter = quarter,
                            weekStart = weekStart.toString(),
                            habitKey = HabitKey.FOCUS,
                            date = today.toString(),
                            completed = true,
                            focusBlocks = newBlockCount
                        )

                        // Update quarterly goal progress
                        repository.updateGoalProgress(
                            quarter = quarter,
                            goalId = focusGoal?.id ?: return@launch,
                            incrementValue = 1.0,
                            lastUpdatedAt = today.toString()
                        )
                    }

                    // Reload to show updated progress
                    loadQuarterData(quarter)
                }
            }

            TodayCheckInAction.OnImpulseTap -> {
                viewModelScope.launch {
                    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                    val impulseControlGoal = _state.value.getGoalByHabit(HabitKey.IMPULSE_CONTROL)

                    // Check if already done today
                    if (impulseControlGoal?.isDoneToday(today) == true) return@launch

                    val quarter = getCurrentQuarter()
                    val weekStart = getWeekStart(today)

                    // Update the impulse control goal
                    val result = repository.updateGoalProgress(
                        quarter = quarter,
                        goalId = impulseControlGoal?.id ?: return@launch,
                        incrementValue = 1.0,
                        lastUpdatedAt = today.toString()
                    )

                    // Update weekly rollup
                    if (result.isSuccess) {
                        repository.updateWeeklyRollup(
                            quarter = quarter,
                            weekStart = weekStart.toString(),
                            habitKey = HabitKey.IMPULSE_CONTROL,
                            date = today.toString(),
                            completed = true
                        )

                        // Increment the quarter's goal days completed counter
                        repository.incrementGoalDaysCompleted(quarter)

                        // Reload data to show updated values
                        loadQuarterData(quarter)
                    }
                }
            }
        }
    }

    private fun getCurrentQuarter(): String {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return "${today.year}-Q${(today.monthNumber - 1) / 3 + 1}"
    }
}
