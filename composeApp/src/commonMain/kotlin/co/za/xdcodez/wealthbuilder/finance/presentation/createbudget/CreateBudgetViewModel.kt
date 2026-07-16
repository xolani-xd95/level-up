package co.za.xdcodez.wealthbuilder.finance.presentation.createbudget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodez.wealthbuilder.common.budgetPeriodFromMonthId
import co.za.xdcodez.wealthbuilder.finance.domain.BudgetRepository
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetCategoryInput
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetMonthModel
import co.za.xdcodez.wealthbuilder.finance.domain.dto.IncomeSourceInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateBudgetViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateBudgetUiState())
    val state = _state.asStateFlow()

    fun init(monthId: String) {
        val period = budgetPeriodFromMonthId(monthId)
        _state.update { it.copy(budgetPeriod = period) }
    }

    fun onAction(action: CreateBudgetActions) {
        when (action) {

            // ── Navigation ───────────────────────────────────────────────────
            CreateBudgetActions.NextStep -> {
                _state.update { it.copy(currentStep = it.currentStep + 1) }
            }

            CreateBudgetActions.PreviousStep -> {
                _state.update { it.copy(currentStep = it.currentStep - 1) }
            }

            // ── Income sources ───────────────────────────────────────────────
            CreateBudgetActions.AddIncomeSource -> {
                _state.update {
                    it.copy(incomeSources = it.incomeSources + IncomeSourceInput())
                }
            }

            is CreateBudgetActions.RemoveIncomeSource -> {
                _state.update {
                    it.copy(
                        incomeSources = it.incomeSources.filter { source ->
                            source.id != action.id
                        }
                    )
                }
            }

            is CreateBudgetActions.UpdateIncomeSourceName -> {
                _state.update {
                    it.copy(
                        incomeSources = it.incomeSources.map { source ->
                            if (source.id == action.id) source.copy(name = action.name)
                            else source
                        }
                    )
                }
            }

            is CreateBudgetActions.UpdateIncomeSourceAmount -> {
                _state.update {
                    it.copy(
                        incomeSources = it.incomeSources.map { source ->
                            if (source.id == action.id) source.copy(amount = action.amount)
                            else source
                        }
                    )
                }
            }

            // ── Categories ───────────────────────────────────────────────────
            CreateBudgetActions.AddCategory -> {
                _state.update {
                    it.copy(categories = it.categories + BudgetCategoryInput())
                }
            }

            is CreateBudgetActions.RemoveCategory -> {
                _state.update {
                    it.copy(
                        categories = it.categories.filter { category ->
                            category.id != action.id
                        }
                    )
                }
            }

            is CreateBudgetActions.UpdateCategoryName -> {
                _state.update {
                    it.copy(
                        categories = it.categories.map { category ->
                            if (category.id == action.id) category.copy(name = action.name)
                            else category
                        }
                    )
                }
            }

            is CreateBudgetActions.UpdateCategoryAmount -> {
                _state.update {
                    it.copy(
                        categories = it.categories.map { category ->
                            if (category.id == action.id) category.copy(amount = action.amount)
                            else category
                        }
                    )
                }
            }

            // ── Save ─────────────────────────────────────────────────────────
            CreateBudgetActions.SaveBudget -> saveBudget()
        }
    }

    private fun saveBudget() {
        viewModelScope.launch {
            val currentState = state.value
            val period = currentState.budgetPeriod ?: return@launch

            val month = BudgetMonthModel(
                monthId = period.monthId,
                startDate = period.startDate.toString(),
                endDate = period.endDate.toString(),
                moneyIn = currentState.totalIncome,
                moneyOut = 0.0,
                totalBudget = currentState.totalAllocated,
                incomeSources = currentState.incomeSources,
                categories = currentState.categories,
            )
            val result = repository.createMonth(month)

            if (result.isSuccess) {
                _state.update { it.copy(isSaved = true) }
            } else {
                _state.update { it.copy(error = "Failed to save budget. Please try again") }
            }
        }
    }
}