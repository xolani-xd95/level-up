package co.za.xdcodes.level_up.journal.presentation.setup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodes.level_up.common.monthId
import co.za.xdcodes.level_up.journal.data.model.MonthlyTradingTarget
import co.za.xdcodes.level_up.journal.domain.JournalRepository
import co.za.xdcodes.level_up.journal.domain.model.TradingConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JournalSetupViewModel(
    private val repository: JournalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(JournalSetupState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<JournalSetupNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun init(monthIndex: Int, year: Int) {
        _state.update { it.copy(monthIndex = monthIndex, year = year) }
        checkExistingConfig()
    }

    private fun checkExistingConfig() {
        viewModelScope.launch {
            val config = repository.getConfig()
            if (config != null) {
                // global config exists — skip step 1
                _state.update {
                    it.copy(
                        isFirstTimeSetup = false,
                        totalSteps = 2,
                        maxTradesPerDay = config.maxTradesPerDay.toString(),
                        lossLimitPercent = (config.lossLimitPercent * 100).toInt().toString(),
                        sessionOneStart = config.sessionOneStart,
                        sessionOneEnd = config.sessionOneEnd,
                        sessionTwoStart = config.sessionTwoStart,
                        sessionTwoEnd = config.sessionTwoEnd
                    )
                }
            }
        }
    }

    fun onAction(action: JournalSetupActions) {
        when (action) {

            // ── Navigation ───────────────────────────────────────
            JournalSetupActions.NextStep -> {
                _state.update { it.copy(currentStep = it.currentStep + 1) }
            }

            JournalSetupActions.PreviousStep -> {
                if (_state.value.currentStep == 1) {
                    viewModelScope.launch {
                        _navigationEvent.emit(JournalSetupNavigationEvent.NavigateBack)
                    }
                } else {
                    _state.update { it.copy(currentStep = it.currentStep - 1) }
                }
            }

            // ── Step 1 — global rules ────────────────────────────
            is JournalSetupActions.OnMaxTradesChanged -> {
                _state.update { it.copy(maxTradesPerDay = action.value) }
            }

            is JournalSetupActions.OnLossLimitChanged -> {
                _state.update { it.copy(lossLimitPercent = action.value) }
            }

            is JournalSetupActions.OnSessionOneStartChanged -> {
                _state.update { it.copy(sessionOneStart = action.value) }
            }

            is JournalSetupActions.OnSessionOneEndChanged -> {
                _state.update { it.copy(sessionOneEnd = action.value) }
            }

            is JournalSetupActions.OnSessionTwoStartChanged -> {
                _state.update { it.copy(sessionTwoStart = action.value) }
            }

            is JournalSetupActions.OnSessionTwoEndChanged -> {
                _state.update { it.copy(sessionTwoEnd = action.value) }
            }

            // ── Step 2 — monthly target ──────────────────────────
            is JournalSetupActions.OnMonthlyTargetChanged -> {
                _state.update { it.copy(monthlyTarget = action.value) }
            }

            // ── Save ─────────────────────────────────────────────
            JournalSetupActions.SaveSetup -> saveSetup()
        }
    }

    private fun saveSetup() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val state = _state.value

            // save global config only on first time setup
            if (state.isFirstTimeSetup) {
                val configResult = repository.saveConfig(
                    TradingConfig(
                        maxTradesPerDay = state.maxTradesPerDay.toIntOrNull() ?: 4,
                        lossLimitPercent = state.lossLimitDecimal,
                        sessionOneStart = state.sessionOneStart,
                        sessionOneEnd = state.sessionOneEnd,
                        sessionTwoStart = state.sessionTwoStart,
                        sessionTwoEnd = state.sessionTwoEnd,
                        isConfigured = true
                    )
                )

                if (configResult.isFailure) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to save config. Please try again."
                        )
                    }
                    return@launch
                }
            }

            // always save monthly target
            val targetResult = repository.saveMonthlyTarget(
                MonthlyTradingTarget(
                    monthId = monthId(state.monthIndex, state.year),
                    monthlyTarget = state.monthlyTarget.toDoubleOrNull() ?: 0.0
                )
            )

            _state.update { it.copy(isLoading = false) }

            if (targetResult.isSuccess) {
                _state.update { it.copy(isSaved = true) }
                _navigationEvent.emit(JournalSetupNavigationEvent.NavigateBack)
            } else {
                _state.update {
                    it.copy(error = "Failed to save target. Please try again.")
                }
            }
        }
    }
}