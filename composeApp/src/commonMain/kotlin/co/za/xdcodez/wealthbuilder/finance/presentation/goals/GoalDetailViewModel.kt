package co.za.xdcodez.wealthbuilder.finance.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodez.wealthbuilder.finance.domain.GoalRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GoalDetailViewModel(
    private val goalRepository: GoalRepository,
    private val goalId: String
) : ViewModel() {

    private val _state = MutableStateFlow(GoalDetailState())
    val state: StateFlow<GoalDetailState> = _state.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<GoalDetailNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        loadGoal()
    }

    fun onAction(action: GoalDetailAction) {
        when (action) {
            GoalDetailAction.ToggleContributionDialog -> toggleContributionDialog()
            is GoalDetailAction.UpdateContributionAmount -> updateContributionAmount(action.amount)
            GoalDetailAction.AddContribution -> addContribution()
            GoalDetailAction.ToggleDeleteConfirmation -> toggleDeleteConfirmation()
            GoalDetailAction.DeleteGoal -> deleteGoal()
            GoalDetailAction.Refresh -> loadGoal()
            GoalDetailAction.DismissError -> dismissError()
        }
    }

    private fun loadGoal() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val goal = goalRepository.getGoal(goalId)
                if (goal != null) {
                    _state.update { it.copy(goal = goal, isLoading = false) }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Goal not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load goal"
                    )
                }
            }
        }
    }

    private fun toggleContributionDialog() {
        _state.update {
            it.copy(
                showContributionDialog = !it.showContributionDialog,
                contributionAmount = ""
            )
        }
    }

    private fun updateContributionAmount(amount: String) {
        if (amount.isEmpty() || amount.toDoubleOrNull() != null) {
            _state.update { it.copy(contributionAmount = amount) }
        }
    }

    private fun addContribution() {
        val amount = _state.value.contributionAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _state.update { it.copy(error = "Please enter a valid amount") }
            return
        }

        viewModelScope.launch {
            try {
                goalRepository.updateGoalProgress(goalId, amount).fold(
                    onSuccess = {
                        _state.update { it.copy(showContributionDialog = false, contributionAmount = "") }
                        loadGoal()
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(error = error.message ?: "Failed to add contribution")
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "An unexpected error occurred")
                }
            }
        }
    }

    private fun toggleDeleteConfirmation() {
        _state.update { it.copy(showDeleteConfirmation = !it.showDeleteConfirmation) }
    }

    private fun deleteGoal() {
        viewModelScope.launch {
            try {
                goalRepository.deleteGoal(goalId).fold(
                    onSuccess = {
                        _navigationEvents.emit(GoalDetailNavigationEvent.NavigateBack)
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                showDeleteConfirmation = false,
                                error = error.message ?: "Failed to delete goal"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        showDeleteConfirmation = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}
