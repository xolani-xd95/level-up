package co.za.xdcodes.level_up.finance.presentation.createbudget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodes.level_up.common.monthId
import co.za.xdcodes.level_up.finance.domain.BudgetRepository
import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryInput
import co.za.xdcodes.level_up.finance.domain.dto.BudgetMonthModel
import co.za.xdcodes.level_up.finance.domain.dto.IncomeSourceInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CreateBudgetViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateBudgetUiState())
    val state = _state.asStateFlow()

    fun init(monthIndex: Int, year: Int) {
        _state.update { it.copy(monthIndex = monthIndex, year = year) }
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

            val state = state.value
            val month = BudgetMonthModel(
                monthId = monthId(state.monthIndex, state.year),
                monthIndex = state.monthIndex,
                year = state.year,
                moneyIn = state.totalIncome,
                moneyOut = 0.0,
                totalBudget = state.totalAllocated,
                incomeSources = state.incomeSources,
                categories = state.categories,
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