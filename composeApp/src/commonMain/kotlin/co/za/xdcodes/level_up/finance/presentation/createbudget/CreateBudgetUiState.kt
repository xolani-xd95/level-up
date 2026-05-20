package co.za.xdcodes.level_up.finance.presentation.createbudget

import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryInput
import co.za.xdcodes.level_up.finance.domain.dto.IncomeSourceInput

data class CreateBudgetUiState(
    val isSaved: Boolean = false,   // ← triggers navigation back on success
    val error: String? = null,
    val currentStep: Int = 1,
    val monthIndex: Int = 1,
    val year: Int = 0,
    val incomeSources: List<IncomeSourceInput> = listOf(
        IncomeSourceInput(name = "Salary")
    ),
    val categories: List<BudgetCategoryInput> = listOf(
        BudgetCategoryInput(name = "Essentials"),
        BudgetCategoryInput(name = "Personal Spending"),
        BudgetCategoryInput(name = "Events"),
        BudgetCategoryInput(name = "Savings"),
        BudgetCategoryInput(name = "Loan Repayments")
    ),
    val isLoading: Boolean = false
) {
    val totalIncome: Double
        get() = incomeSources.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }

    val totalAllocated: Double
        get() = categories.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }

    val unallocated: Double
        get() = totalIncome - totalAllocated

    val isOverAllocated: Boolean
        get() = totalAllocated > totalIncome

    val allocationProgress: Float
        get() = (totalAllocated / totalIncome.coerceAtLeast(1.0))
            .coerceIn(0.0, 1.0).toFloat()

    val isStep1Valid: Boolean
        get() = incomeSources.all {
            it.name.isNotBlank() && (it.amount.toDoubleOrNull() ?: 0.0) > 0.0
        }

    val isStep2Valid: Boolean
        get() = categories.all {
            it.name.isNotBlank() && (it.amount.toDoubleOrNull() ?: 0.0) > 0.0
        } && !isOverAllocated
}