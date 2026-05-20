package co.za.xdcodes.level_up.finance.domain

import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryInput
import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodes.level_up.finance.domain.dto.BudgetMonthModel
import co.za.xdcodes.level_up.finance.domain.dto.CategoryTransaction

interface BudgetRepository {

    // ── Month ────────────────────────────────────────────────────
    suspend fun getMonth(monthId: String): BudgetMonthModel?
    suspend fun createMonth(month: BudgetMonthModel): Result<Unit>

    // ── Categories ───────────────────────────────────────────────
    suspend fun getCategory(monthId: String, categoryId: String): BudgetCategoryModel?
    suspend fun getCategories(monthId: String): List<BudgetCategoryModel>
    suspend fun addCategory(monthId: String, category: BudgetCategoryInput): Result<Unit>
    suspend fun updateCategory(monthId: String, categoryId: String, name: String, budget: Double): Result<Unit>
    suspend fun deleteCategory(monthId: String, categoryId: String): Result<Unit>

    // ── Transactions ─────────────────────────────────────────────
    suspend fun getTransactions(monthId: String, categoryId: String): List<CategoryTransaction>
    suspend fun addTransaction(monthId: String, categoryId: String, transaction: CategoryTransaction): Result<Unit>
    suspend fun deleteTransaction(monthId: String, categoryId: String, transactionId: String): Result<Unit>
    suspend fun payTransaction(monthId: String, categoryId: String, transactionId: String): Result<Unit>
    suspend fun unpayTransaction(monthId: String, categoryId: String, transactionId: String): Result<Unit>
}