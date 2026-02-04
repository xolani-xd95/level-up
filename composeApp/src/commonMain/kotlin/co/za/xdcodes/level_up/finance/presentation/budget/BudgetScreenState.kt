package co.za.xdcodes.level_up.finance.presentation.budget

import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodes.level_up.finance.domain.dto.BudgetOverviewModel
import co.za.xdcodes.level_up.finance.domain.dto.CategoryTransaction

data class BudgetScreenState(
    val budgetMonths: List<BudgetOverviewModel> = emptyList(),
    val selectedMonth: BudgetOverviewModel? = null,
    val budgetCategories: List<BudgetCategoryModel> = emptyList(),
    val selectedCategory: String? = null,
    val showAddTransactionBottomSheet: Boolean = false,
    val categoryTransactions: List<CategoryTransaction> = emptyList()
)

sealed interface BudgetScreenActions {
    data class OnBudgetCardClicked(val month: String) : BudgetScreenActions
    data class OnCategoryCardClicked(val category: String) : BudgetScreenActions
    data class OnAddTransaction(val transaction: CategoryTransaction) : BudgetScreenActions
    data class OnShowAddTransactionBottomSheet(val show: Boolean) : BudgetScreenActions
    data class OnPayTransaction(val transactionId: String) : BudgetScreenActions
}

sealed interface BudgetScreenNavigation {
    data object NavigateUp : BudgetScreenNavigation
    data object NavigateToTransactions: BudgetScreenNavigation

}