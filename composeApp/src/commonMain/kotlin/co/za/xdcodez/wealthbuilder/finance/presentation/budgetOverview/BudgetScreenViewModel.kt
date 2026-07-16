package co.za.xdcodez.wealthbuilder.finance.presentation.budgetOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodez.wealthbuilder.common.BudgetPeriod
import co.za.xdcodez.wealthbuilder.common.getCurrentBudgetPeriod
import co.za.xdcodez.wealthbuilder.common.getNextBudgetPeriod
import co.za.xdcodez.wealthbuilder.common.getPreviousBudgetPeriod
import co.za.xdcodez.wealthbuilder.finance.domain.BudgetRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BudgetScreenViewModel(
    private val repository: BudgetRepository
) : ViewModel() {
    private val _state = MutableStateFlow(BudgetScreenState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<BudgetScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        val currentPeriod = getCurrentBudgetPeriod()
        _state.update {
            it.copy(
                currentPeriod = currentPeriod,
                selectedPeriod = currentPeriod
            )
        }
        fetchPeriodData(currentPeriod)
    }

    fun onAction(action: BudgetScreenActions) {
        when (action) {
            is BudgetScreenActions.NavigateToTransactions -> {
                viewModelScope.launch {
                    _navigationEvent.emit(
                        BudgetScreenNavigationEvent.ToTransactions(
                            monthId = action.monthId,
                            categoryId = action.categoryId
                        )
                    )
                }
            }

            is BudgetScreenActions.NavigateToBudgetSetup -> {
                viewModelScope.launch {
                    _navigationEvent.emit(
                        BudgetScreenNavigationEvent.ToBudgetSetup(
                            monthId = action.monthId
                        )
                    )
                }
            }

            BudgetScreenActions.OnPreviousPeriod -> navigatePeriod(direction = -1)
            BudgetScreenActions.OnNextPeriod -> navigatePeriod(direction = +1)
        }
    }

    private fun navigatePeriod(direction: Int) {
        val currentPeriod = state.value.selectedPeriod ?: return

        val newPeriod = if (direction > 0) {
            getNextBudgetPeriod(currentPeriod)
        } else {
            getPreviousBudgetPeriod(currentPeriod)
        }

        _state.update {
            it.copy(
                selectedPeriod = newPeriod,
                selectedMonth = null,
                budgetCategories = emptyList()
            )
        }

        fetchPeriodData(newPeriod)
    }

    private fun fetchPeriodData(period: BudgetPeriod) {
        _state.update { it.copy(isLoading = true) }
        println("BudgetScreenViewModel Fetching data for period: ${period.monthId}")
        viewModelScope.launch {
            val monthId = period.monthId
            val monthData = repository.getMonth(monthId)
            val categories = if (monthData != null) {
                repository.getCategories(monthId)
                    .sortedBy { category ->
                        if (category.budget > 0) category.totalPaid / category.budget else 0.0
                    }
            } else emptyList()

            _state.update {
                it.copy(
                    selectedMonth = monthData,
                    isLoading = false,
                    budgetCategories = categories
                )
            }
        }
    }

    fun onReturnFromSetup() {
        state.value.selectedPeriod?.let { fetchPeriodData(it) }
    }
}