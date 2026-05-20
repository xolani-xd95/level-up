package co.za.xdcodes.level_up.finance.presentation.budgetOverview

import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodes.level_up.finance.domain.dto.BudgetMonthModel
import co.za.xdcodes.level_up.finance.domain.dto.CategoryTransaction
import co.za.xdcodes.level_up.finance.domain.dto.TransactionInput
import co.za.xdcodes.level_up.finance.presentation.budgetTransactions.TransactionsActions

data class BudgetScreenState(
    val selectedMonth: BudgetMonthModel? = null,
    val currentMonthIndex: Int = 0,   // 1-12
    val currentYear: Int = 0,
    val selectedMonthIndex: Int = 0,  // 1-12, changes on arrow tap
    val selectedYear: Int = 0,
    val budgetCategories: List<BudgetCategoryModel> = emptyList(),
    val selectedCategory: BudgetCategoryModel? = null,

    val isLoading: Boolean = false,
)

sealed interface BudgetScreenNavigationEvent {
    data class ToTransactions(val monthId: String, val categoryId: String) : BudgetScreenNavigationEvent
    data class ToBudgetSetup(val monthIndex: Int, val year: Int) : BudgetScreenNavigationEvent
}
sealed interface BudgetScreenActions {
    data class NavigateToTransactions(val monthId: String, val categoryId: String) : BudgetScreenActions
    data class NavigateToBudgetSetup(val monthIndex: Int, val year: Int) : BudgetScreenActions
    object OnPreviousMonth : BudgetScreenActions
    object OnNextMonth : BudgetScreenActions
}