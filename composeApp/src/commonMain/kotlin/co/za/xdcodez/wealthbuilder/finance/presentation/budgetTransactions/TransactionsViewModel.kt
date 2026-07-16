package co.za.xdcodez.wealthbuilder.finance.presentation.budgetTransactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodez.wealthbuilder.finance.domain.BudgetRepository
import co.za.xdcodez.wealthbuilder.finance.domain.dto.CategoryTransaction
import co.za.xdcodez.wealthbuilder.finance.domain.dto.TransactionInput
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionsViewModel(
    val repository: BudgetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionsUiState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<TransactionNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun init(monthId: String, categoryId: String) {
        _state.update { it.copy(monthId = monthId) }
        getCategory(monthId, categoryId)
        getTransactions(monthId, categoryId)
    }

    fun getCategory(monthId: String, categoryId: String) {
        viewModelScope.launch {
            val category = repository.getCategory(monthId, categoryId)
            _state.update {
                it.copy(
                    category = category
                )
            }
        }
    }

    fun getTransactions(monthId: String, categoryId: String) {
        viewModelScope.launch {
            val transactions = repository.getTransactions(monthId, categoryId)
            _state.update {
                it.copy(
                    categoryTransactions = transactions
                )
            }
        }
    }

    fun onAction(action: TransactionsActions) {
        when (action) {
            is TransactionsActions.OnPayTransaction -> payTransaction(action.transactionId)
            is TransactionsActions.OnTransactionClicked -> _state.update {
                it.copy(
                    selectedTransaction = action.transaction
                )
            }

            is TransactionsActions.OnTransactionNameChanged -> _state.update {
                it.copy(
                    transactionInput = it.transactionInput.copy(name = action.name)
                )
            }

            is TransactionsActions.OnTransactionAmountChanged -> _state.update {
                it.copy(
                    transactionInput = it.transactionInput.copy(amount = action.amount)
                )
            }

            TransactionsActions.OnDismissTransactionSheet -> _state.update {
                it.copy(
                    selectedTransaction = null,
                    editingTransaction = null
                )
            }

            TransactionsActions.OnSubmitTransaction -> addTransaction()
            is TransactionsActions.OnDeleteCategory -> deleteCategory(action.categoryId)
            is TransactionsActions.OnUpdateCategory -> updateCategory(
                categoryId = action.categoryId,
                name = action.name,
                budget = action.budget
            )

            is TransactionsActions.OnEditTransaction -> _state.update {
                it.copy(
                    editingTransaction = action.transaction,
                    editTransactionInput = TransactionInput(
                        name = action.transaction.name,
                        amount = action.transaction.amount.toString()
                    ),
                    selectedTransaction = null
                )
            }

            is TransactionsActions.OnEditTransactionNameChanged -> _state.update {
                it.copy(
                    editTransactionInput = it.editTransactionInput.copy(name = action.name)
                )
            }

            is TransactionsActions.OnEditTransactionAmountChanged -> _state.update {
                it.copy(
                    editTransactionInput = it.editTransactionInput.copy(amount = action.amount)
                )
            }

            TransactionsActions.OnSubmitEditTransaction -> updateTransaction()
            is TransactionsActions.OnDeleteTransaction -> deleteTransaction(action.transactionId)
        }
    }

    private fun updateCategory(categoryId: String, name: String, budget: Double) {
        state.value.monthId?.let { monthId ->
            viewModelScope.launch {
                val result = repository.updateCategory(
                    monthId = monthId,
                    categoryId = categoryId,
                    name = name,
                    budget = budget
                )
                if (result.isSuccess) {
                    // refresh category header
                    val updated = repository.getCategory(monthId, categoryId)
                    _state.update { it.copy(category = updated) }
                }
            }
        }
    }

    private fun deleteCategory(categoryId: String) {
        state.value.monthId?.let { monthId ->
            viewModelScope.launch {
                val result = repository.deleteCategory(monthId, categoryId)
                if (result.isSuccess) {
                    _navigationEvent.emit(TransactionNavigationEvent.NavigateBack)
                }
            }
        }
    }

    private fun addTransaction() {
        val input = state.value.transactionInput
        val category = state.value.category ?: return
        val monthId = state.value.monthId ?: return

        viewModelScope.launch {
            val transaction = CategoryTransaction(
                name = input.name,
                amount = input.amount.toDoubleOrNull() ?: 0.0,
                isPaid = false
            )

            val result = repository.addTransaction(
                monthId = monthId,
                categoryId = category.id,
                transaction = transaction
            )

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        transactionInput = TransactionInput()
                    )
                }
                refreshTransactionScreen(monthId, category.id)
            }
        }
    }

    fun payTransaction(transactionId: String) {
        viewModelScope.launch {
            val result = repository.payTransaction(
                transactionId = transactionId,
                monthId = state.value.monthId.orEmpty(),
                categoryId = state.value.category?.id.orEmpty()
            )

            if (result.isSuccess) {
                refreshTransactionScreen(
                    state.value.monthId.orEmpty(),
                    state.value.category?.id.orEmpty()
                )
            }
        }
    }

    private fun refreshTransactionScreen(monthId: String, categoryId: String) {
        viewModelScope.launch {
            val category = repository.getCategory(monthId,  categoryId)
            val transactions = repository.getTransactions(monthId, categoryId)
            _state.update {
                it.copy(
                    categoryTransactions = transactions,
                    category = category
                )
            }
        }
    }

    private fun updateTransaction() {
        val editInput = state.value.editTransactionInput
        val editingTransaction = state.value.editingTransaction ?: return
        val category = state.value.category ?: return
        val monthId = state.value.monthId ?: return

        viewModelScope.launch {
            val result = repository.updateTransaction(
                monthId = monthId,
                categoryId = category.id,
                transactionId = editingTransaction.id,
                name = editInput.name,
                amount = editInput.amount.toDoubleOrNull() ?: 0.0
            )

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        editingTransaction = null,
                        editTransactionInput = TransactionInput()
                    )
                }
                refreshTransactionScreen(monthId, category.id)
            }
        }
    }

    private fun deleteTransaction(transactionId: String) {
        val category = state.value.category ?: return
        val monthId = state.value.monthId ?: return

        viewModelScope.launch {
            val result = repository.deleteTransaction(
                monthId = monthId,
                categoryId = category.id,
                transactionId = transactionId
            )

            if (result.isSuccess) {
                refreshTransactionScreen(monthId, category.id)
            }
        }
    }
}