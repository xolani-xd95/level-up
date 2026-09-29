package co.za.xdcodez.wealthbuilder.finance.presentation.goals

import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetCategoryModel

data class CreateGoalState(
    val name: String = "",
    val targetAmount: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val estimatedMonthlyContribution: Double = 0.0,
    val availableCategories: List<BudgetCategoryModel> = emptyList(),
    val selectedCategoryId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCategorySelector: Boolean = false
) {
    val isValid: Boolean
        get() = name.isNotBlank() &&
                targetAmount.toDoubleOrNull()?.let { it > 0 } == true &&
                startDate.isNotBlank() &&
                endDate.isNotBlank()
}

sealed class CreateGoalAction {
    data class UpdateName(val name: String) : CreateGoalAction()
    data class UpdateTargetAmount(val amount: String) : CreateGoalAction()
    data class UpdateStartDate(val date: String) : CreateGoalAction()
    data class UpdateEndDate(val date: String) : CreateGoalAction()
    data class SelectCategory(val categoryId: String?) : CreateGoalAction()
    data object ToggleCategorySelector : CreateGoalAction()
    data object CreateGoal : CreateGoalAction()
    data object DismissError : CreateGoalAction()
}

sealed class CreateGoalNavigationEvent {
    data object NavigateBack : CreateGoalNavigationEvent()
    data class NavigateToGoalDetail(val goalId: String) : CreateGoalNavigationEvent()
}
