package co.za.xdcodez.wealthbuilder.finance.presentation.goals

import co.za.xdcodez.wealthbuilder.finance.domain.dto.Goal

data class GoalDetailState(
    val goal: Goal? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showContributionDialog: Boolean = false,
    val contributionAmount: String = "",
    val showDeleteConfirmation: Boolean = false
) {
    val isContributionValid: Boolean
        get() = contributionAmount.toDoubleOrNull()?.let { it > 0 } == true
}

sealed class GoalDetailAction {
    data object ToggleContributionDialog : GoalDetailAction()
    data class UpdateContributionAmount(val amount: String) : GoalDetailAction()
    data object AddContribution : GoalDetailAction()
    data object ToggleDeleteConfirmation : GoalDetailAction()
    data object DeleteGoal : GoalDetailAction()
    data object Refresh : GoalDetailAction()
    data object DismissError : GoalDetailAction()
}

sealed class GoalDetailNavigationEvent {
    data object NavigateBack : GoalDetailNavigationEvent()
}
