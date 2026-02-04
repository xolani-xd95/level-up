package co.za.xdcodes.level_up.finance.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class BudgetCategoryModel(
    val category: String,
    val totalPaid: Double,
    val totalBudget : Double,
    val month: String,
) {
    val budgetRemaining: Double
        get() = totalBudget - totalPaid
}

@Serializable
data class CategoryTransaction(
    val id: String,
    val name: String,
    val category: String,
    val amount: Double,
    val month:String,
    val date: String,
    val isPaid: Boolean
)