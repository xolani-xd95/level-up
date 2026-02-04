package co.za.xdcodes.level_up.finance.domain

import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodes.level_up.finance.domain.dto.BudgetOverviewModel
import co.za.xdcodes.level_up.finance.domain.dto.CategoryTransaction

interface BudgetRepository {

    suspend fun getBudgetMonths(): List<BudgetOverviewModel>
    suspend fun getBudgetCategories(month: String): List<BudgetCategoryModel>
    suspend fun addTransaction(
        transaction: CategoryTransaction,
        category: BudgetCategoryModel
    ): Result<Unit>

    suspend fun getCategoryTransaction(month: String, category: String): List<CategoryTransaction>
    suspend fun updateCategoryField(
        month: String,
        category: String,
        field: String,
        value: Double
    )

    suspend fun payTransaction(
        category: String,
        month: String,
        transactionId: String
    ): Result<Unit>
}