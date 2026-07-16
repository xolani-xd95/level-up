package co.za.xdcodez.wealthbuilder.finance.presentation.budgetOverview

import co.za.xdcodez.wealthbuilder.common.BudgetPeriod
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetMonthModel

data class BudgetScreenState(
    val selectedMonth: BudgetMonthModel? = null,
    val currentPeriod: BudgetPeriod? = null,   // The period containing today
    val selectedPeriod: BudgetPeriod? = null,  // The period being viewed (changes on navigation)
    val budgetCategories: List<BudgetCategoryModel> = emptyList(),
    val selectedCategory: BudgetCategoryModel? = null,
    val isLoading: Boolean = false,
)

sealed interface BudgetScreenNavigationEvent {
    data class ToTransactions(val monthId: String, val categoryId: String) : BudgetScreenNavigationEvent
    data class ToBudgetSetup(val monthId: String) : BudgetScreenNavigationEvent
}

sealed interface BudgetScreenActions {
    data class NavigateToTransactions(val monthId: String, val categoryId: String) : BudgetScreenActions
    data class NavigateToBudgetSetup(val monthId: String) : BudgetScreenActions
    object OnPreviousPeriod : BudgetScreenActions
    object OnNextPeriod : BudgetScreenActions
}