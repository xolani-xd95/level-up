package co.za.xdcodes.level_up.dashboard.domain.dto

data class DailySummaryModel(
    var isoDate: String,
    var success: Boolean,
    val isToday: Boolean = false,
    val isFuture: Boolean = false
)

enum class DayStatus {
    SUCCESS,
    PARTIAL,
    FAILED,
    NONE
}