package co.za.xdcodez.wealthbuilder.finance.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodez.wealthbuilder.common.getCurrentBudgetPeriod
import co.za.xdcodez.wealthbuilder.finance.domain.BudgetRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BudgetDashboardViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BudgetDashboardState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<BudgetDashboardNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        loadDashboardData()
    }

    fun onAction(action: BudgetDashboardActions) {
        when (action) {
            BudgetDashboardActions.ViewBudgetDetails -> {
                viewModelScope.launch {
                    _navigationEvent.emit(BudgetDashboardNavigationEvent.ToBudgetDetails)
                }
            }
            BudgetDashboardActions.CreateNewGoal -> {
                viewModelScope.launch {
                    _navigationEvent.emit(BudgetDashboardNavigationEvent.ToCreateGoal)
                }
            }
            is BudgetDashboardActions.OnGoalClick -> {
                viewModelScope.launch {
                    _navigationEvent.emit(BudgetDashboardNavigationEvent.ToGoalDetail(action.goalId))
                }
            }
            BudgetDashboardActions.Refresh -> {
                loadDashboardData()
            }
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentPeriod = getCurrentBudgetPeriod()
//            val monthResult = repository.getMonth(currentPeriod.monthId)
            val monthResult = repository.getMonth("2026-07-27")

            // TODO: Load goals from repository when implemented
            val goals = emptyList<co.za.xdcodez.wealthbuilder.finance.domain.dto.Goal>()

            _state.update {
                it.copy(
                    isLoading = false,
                    currentMonthSummary = monthResult,
                    goals = goals
                )
            }
        }
    }
}

sealed interface BudgetDashboardNavigationEvent {
    object ToBudgetDetails : BudgetDashboardNavigationEvent
    object ToCreateGoal : BudgetDashboardNavigationEvent
    data class ToGoalDetail(val goalId: String) : BudgetDashboardNavigationEvent
}
