package co.za.xdcodez.wealthbuilder.journal.domain.model

enum class OverrideReason {
    AFTER_TARGET,
    AFTER_LOSS_LIMIT,
    AFTER_MAX_TRADES,
    OUTSIDE_SESSION
}
