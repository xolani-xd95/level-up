package co.za.xdcodes.level_up.finance.presentation.createbudget

sealed interface CreateBudgetActions {

    // ── Step navigation ──────────────────────────────────────────
    object NextStep : CreateBudgetActions
    object PreviousStep : CreateBudgetActions

    // ── Income sources ───────────────────────────────────────────
    object AddIncomeSource : CreateBudgetActions
    data class RemoveIncomeSource(val id: String) : CreateBudgetActions
    data class UpdateIncomeSourceName(val id: String, val name: String) : CreateBudgetActions
    data class UpdateIncomeSourceAmount(val id: String, val amount: String) : CreateBudgetActions

    // ── Categories ───────────────────────────────────────────────
    object AddCategory : CreateBudgetActions
    data class RemoveCategory(val id: String) : CreateBudgetActions
    data class UpdateCategoryName(val id: String, val name: String) : CreateBudgetActions
    data class UpdateCategoryAmount(val id: String, val amount: String) : CreateBudgetActions

    // ── Final save ───────────────────────────────────────────────
    object SaveBudget : CreateBudgetActions
}