package co.za.xdcodes.level_up.finance.presentation.budgetOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodes.level_up.common.monthId
import co.za.xdcodes.level_up.finance.domain.BudgetRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class BudgetScreenViewModel(
    private val repository: BudgetRepository
) : ViewModel() {
    private val _state = MutableStateFlow(BudgetScreenState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<BudgetScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        _state.update {
            it.copy(
                currentMonthIndex = today.monthNumber,
                currentYear = today.year,
                selectedMonthIndex = today.monthNumber,
                selectedYear = today.year
            )
        }
        fetchMonthData(
            month = today.monthNumber,
            year = today.year
        )
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
                            monthIndex = action.monthIndex,
                            year = action.year
                        )
                    )
                }
            }

            BudgetScreenActions.OnPreviousMonth -> navigateMonth(direction = -1)
            BudgetScreenActions.OnNextMonth -> navigateMonth(direction = +1)
        }
    }

    private fun navigateMonth(direction: Int) {
        val currentMonth = state.value.selectedMonthIndex
        val currentYear = state.value.selectedYear

        // Calculate new month and year, handling year rollover
        val newMonth = currentMonth + direction
        val (resolvedMonth, resolvedYear) = when {
            newMonth < 1 -> 12 to currentYear - 1  // January → December previous year
            newMonth > 12 -> 1 to currentYear + 1   // December → January next year
            else -> newMonth to currentYear
        }

        _state.update {
            it.copy(
                selectedMonthIndex = resolvedMonth,
                selectedYear = resolvedYear,
                selectedMonth = null,           // clear while loading
                budgetCategories = emptyList()  // clear while loading
            )
        }

        fetchMonthData(month = resolvedMonth, year = resolvedYear)
    }

    private fun fetchMonthData(month: Int, year: Int) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {

            val id = monthId(month, year)
            val monthData = repository.getMonth(id)
            val categories = if (monthData != null) repository.getCategories(id)
            else emptyList()

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
        fetchMonthData(
            month = state.value.selectedMonthIndex,
            year = state.value.selectedYear
        )
    }
}