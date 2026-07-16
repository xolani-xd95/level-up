package co.za.xdcodez.wealthbuilder.finance.presentation.budgetTransactions

import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodez.wealthbuilder.finance.domain.dto.CategoryTransaction
import co.za.xdcodez.wealthbuilder.finance.domain.dto.TransactionInput

data class TransactionsUiState(
    val monthId: String? = "",
    val category: BudgetCategoryModel? = null,
    val categoryTransactions: List<CategoryTransaction> = emptyList(),
    val selectedTransaction: CategoryTransaction? = null,
    val transactionInput: TransactionInput = TransactionInput(),
    val editingTransaction: CategoryTransaction? = null,
    val editTransactionInput: TransactionInput = TransactionInput()
)

sealed interface TransactionNavigationEvent {
    object NavigateBack : TransactionNavigationEvent
}

sealed interface TransactionsActions {
    data class OnTransactionClicked(val transaction: CategoryTransaction) : TransactionsActions
    data class OnPayTransaction(val transactionId: String) : TransactionsActions
    data class OnTransactionNameChanged(val name: String) : TransactionsActions
    data class OnTransactionAmountChanged(val amount: String) : TransactionsActions
    data class OnUpdateCategory(
        val categoryId: String,
        val name: String,
        val budget: Double
    ) : TransactionsActions

    data class OnDeleteCategory(val categoryId: String) : TransactionsActions
    object OnSubmitTransaction : TransactionsActions
    object OnDismissTransactionSheet : TransactionsActions

    data class OnEditTransaction(val transaction: CategoryTransaction) : TransactionsActions
    data class OnEditTransactionNameChanged(val name: String) : TransactionsActions
    data class OnEditTransactionAmountChanged(val amount: String) : TransactionsActions
    object OnSubmitEditTransaction : TransactionsActions
    data class OnDeleteTransaction(val transactionId: String) : TransactionsActions
}