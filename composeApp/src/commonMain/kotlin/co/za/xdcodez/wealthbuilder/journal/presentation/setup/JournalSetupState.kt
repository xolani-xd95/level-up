package co.za.xdcodez.wealthbuilder.journal.presentation.setup

data class JournalSetupState(
    val currentStep: Int = 1,
    val totalSteps: Int = 3,        // 2 if config exists
    val isFirstTimeSetup: Boolean = true,
    val monthIndex: Int = 1,
    val year: Int = 2026,

    // step 1 — global rules
    val maxTradesPerDay: String = "4",
    val lossLimitPercent: String = "50",
    val sessionOneStart: String = "09:00",
    val sessionOneEnd: String = "12:00",
    val sessionTwoStart: String = "15:00",
    val sessionTwoEnd: String = "16:30",

    // step 2 — monthly target
    val monthlyTarget: String = "20000",

    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
) {
    // derived
    val dailyTarget: Double
        get() = (monthlyTarget.toDoubleOrNull() ?: 0.0) / 20.0

    val weeklyTarget: Double
        get() = dailyTarget * 5

    val lossLimitAmount: Double
        get() = dailyTarget * 0.5

    val lossLimitDecimal: Double
        get() = (lossLimitPercent.toDoubleOrNull() ?: 50.0) / 100

    val isStep1Valid: Boolean
        get() = maxTradesPerDay.toIntOrNull() != null &&
                (lossLimitPercent.toDoubleOrNull() ?: 0.0) > 0.0 &&
                sessionOneStart.isNotBlank() &&
                sessionOneEnd.isNotBlank() &&
                sessionTwoStart.isNotBlank() &&
                sessionTwoEnd.isNotBlank()

    val isStep2Valid: Boolean
        get() = (monthlyTarget.toDoubleOrNull() ?: 0.0) > 0.0
}

sealed interface JournalSetupActions {
    object NextStep : JournalSetupActions
    object PreviousStep : JournalSetupActions
    data class OnLossLimitChanged(val value: String) : JournalSetupActions
    data class OnMaxTradesChanged(val value: String) : JournalSetupActions
    data class OnSessionOneStartChanged(val value: String) : JournalSetupActions
    data class OnSessionOneEndChanged(val value: String) : JournalSetupActions
    data class OnSessionTwoStartChanged(val value: String) : JournalSetupActions
    data class OnSessionTwoEndChanged(val value: String) : JournalSetupActions
    data class OnMonthlyTargetChanged(val value: String) : JournalSetupActions
    object SaveSetup : JournalSetupActions
}

sealed interface JournalSetupNavigationEvent {
    object NavigateBack : JournalSetupNavigationEvent
}