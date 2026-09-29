package co.za.xdcodez.wealthbuilder.finance.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodez.wealthbuilder.finance.domain.BudgetRepository
import co.za.xdcodez.wealthbuilder.finance.domain.GoalRepository
import co.za.xdcodez.wealthbuilder.finance.domain.dto.Goal
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class CreateGoalViewModel(
    private val goalRepository: GoalRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateGoalState())
    val state: StateFlow<CreateGoalState> = _state.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<CreateGoalNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        loadCategories()
    }

    fun onAction(action: CreateGoalAction) {
        when (action) {
            is CreateGoalAction.UpdateName -> updateName(action.name)
            is CreateGoalAction.UpdateTargetAmount -> updateTargetAmount(action.amount)
            is CreateGoalAction.UpdateStartDate -> updateStartDate(action.date)
            is CreateGoalAction.UpdateEndDate -> updateEndDate(action.date)
            is CreateGoalAction.SelectCategory -> selectCategory(action.categoryId)
            CreateGoalAction.ToggleCategorySelector -> toggleCategorySelector()
            CreateGoalAction.CreateGoal -> createGoal()
            CreateGoalAction.DismissError -> dismissError()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val monthId = "${today.year}-${today.monthNumber.toString().padStart(2, '0')}"
                val categories = budgetRepository.getCategories(monthId)
                _state.update { it.copy(availableCategories = categories) }
            } catch (e: Exception) {
                // Silently fail - categories are optional
            }
        }
    }

    private fun updateName(name: String) {
        _state.update { it.copy(name = name) }
    }

    private fun updateTargetAmount(amount: String) {
        // Only allow valid number input
        if (amount.isEmpty() || amount.toDoubleOrNull() != null) {
            _state.update { it.copy(targetAmount = amount) }
            calculateMonthlyContribution()
        }
    }

    private fun updateStartDate(date: String) {
        _state.update { it.copy(startDate = date) }
        calculateMonthlyContribution()
    }

    private fun updateEndDate(date: String) {
        _state.update { it.copy(endDate = date) }
        calculateMonthlyContribution()
    }

    private fun calculateMonthlyContribution() {
        val currentState = _state.value
        if (currentState.startDate.isEmpty() || currentState.endDate.isEmpty()) {
            _state.update { it.copy(estimatedMonthlyContribution = 0.0) }
            return
        }

        val targetAmount = currentState.targetAmount.toDoubleOrNull() ?: 0.0
        if (targetAmount <= 0) {
            _state.update { it.copy(estimatedMonthlyContribution = 0.0) }
            return
        }

        try {
            val start = LocalDate.parse(currentState.startDate)
            val end = LocalDate.parse(currentState.endDate)

            val monthsDiff = ((end.year - start.year) * 12 + (end.monthNumber - start.monthNumber)).coerceAtLeast(1)
            val monthly = targetAmount / monthsDiff

            _state.update { it.copy(estimatedMonthlyContribution = monthly) }
        } catch (e: Exception) {
            _state.update { it.copy(estimatedMonthlyContribution = 0.0) }
        }
    }

    private fun selectCategory(categoryId: String?) {
        _state.update { it.copy(selectedCategoryId = categoryId, showCategorySelector = false) }
    }

    private fun toggleCategorySelector() {
        _state.update { it.copy(showCategorySelector = !it.showCategorySelector) }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    private fun createGoal() {
        val currentState = _state.value

        if (!currentState.isValid) {
            _state.update { it.copy(error = "Please fill in all required fields") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val goal = Goal(
                    name = currentState.name,
                    targetAmount = currentState.targetAmount.toDouble(),
                    currentAmount = 0.0,
                    startDate = currentState.startDate,
                    endDate = currentState.endDate,
                    linkedCategoryId = currentState.selectedCategoryId
                )

                goalRepository.createGoal(goal).fold(
                    onSuccess = {
                        _state.update { it.copy(isLoading = false) }
                        _navigationEvents.emit(CreateGoalNavigationEvent.NavigateToGoalDetail(goal.id))
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to create goal"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }
}
