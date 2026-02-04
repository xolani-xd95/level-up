package co.za.xdcodes.level_up.finance.presentation.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import co.za.xdcodes.level_up.finance.domain.BudgetRepository
import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodes.level_up.finance.domain.dto.CategoryTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Month

class BudgetScreenViewModel(
    private val repository: BudgetRepository
) : ViewModel() {
    private val _state = MutableStateFlow(BudgetScreenState())
    val state = _state.asStateFlow()


    init {
        getAllBudgetMonths()
    }

    fun onAction(action: BudgetScreenActions) {
        when (action) {
            is BudgetScreenActions.OnBudgetCardClicked -> {
                _state.update {
                    it.copy(
                        selectedMonth = state.value.budgetMonths.firstOrNull { month ->
                            month.month.contains(
                                action.month
                            )
                        }
                    )
                }
                getBudgetCategories(action.month)
            }

            is BudgetScreenActions.OnCategoryCardClicked -> {
                _state.update {
                    it.copy(selectedCategory = action.category)
                }
            }

            is BudgetScreenActions.OnShowAddTransactionBottomSheet -> {
                _state.update {
                    it.copy(
                        showAddTransactionBottomSheet = action.show
                    )
                }
            }

            is BudgetScreenActions.OnAddTransaction -> addTransaction(action.transaction)
            is BudgetScreenActions.OnPayTransaction -> payTransaction(action.transactionId)
        }
    }


    fun payTransaction(transactionId: String) {
        val categoryModel = state.value.budgetCategories.firstOrNull { category ->
            category.category.contains(state.value.selectedCategory.orEmpty())
        }
        viewModelScope.launch {
            val result = repository.payTransaction(
                month = categoryModel?.month.orEmpty(),
                category = categoryModel?.category.orEmpty(),
                transactionId = transactionId
            )

            if(result.isSuccess) {
                refreshTransactionScreen(categoryModel?.month.orEmpty())
            }
        }
    }

    fun getTransactions() {
        val categoryModel = state.value.budgetCategories.firstOrNull { category ->
            category.category.contains(state.value.selectedCategory.orEmpty())
        }
        val month = categoryModel?.month.orEmpty()
        val category = categoryModel?.category.orEmpty()

        viewModelScope.launch {
            val transactions = repository.getCategoryTransaction(month, category)
            _state.update {
                it.copy(
                    categoryTransactions = transactions
                )
            }
        }
    }

    private fun addTransaction(transaction: CategoryTransaction) {
        viewModelScope.launch {
            val categoryModel = state.value.budgetCategories.firstOrNull { category ->
                category.category.contains(state.value.selectedCategory.orEmpty())
            }

            val result = repository.addTransaction(
                transaction = transaction.copy(
                    category = categoryModel?.category.orEmpty(),
                    month = categoryModel?.month.orEmpty()
                ),
                category = categoryModel!!
            )
            if (result.isSuccess) {
                refreshTransactionScreen(categoryModel.month)
            }
        }
    }

    private fun refreshTransactionScreen(month: String) {
        getTransactions()
        getBudgetCategories(month)
        _state.update {
            it.copy(
                showAddTransactionBottomSheet = false,
            )
        }
    }

    private fun getAllBudgetMonths() {
        viewModelScope.launch {
            val months = repository.getBudgetMonths()
            _state.update {
                it.copy(
                    budgetMonths = months
                )
            }
        }
    }

    fun getBudgetCategories(month: String) {
        viewModelScope.launch {
            val categories = repository.getBudgetCategories(month)
            _state.update {
                it.copy(
                    budgetCategories = categories
                )
            }
        }
    }
}