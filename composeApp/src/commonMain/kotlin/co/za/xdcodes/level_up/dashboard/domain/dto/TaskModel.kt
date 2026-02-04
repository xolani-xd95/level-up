package co.za.xdcodes.level_up.dashboard.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskModel(
    val id: String,
    val content: String,
    val duration: String,
    val category: TaskCategory,
    val date: String,
    val state: TaskState,
    val priority: TaskPriority,
)

@Serializable
enum class TaskPriority(val priority: String) {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH")
}
@Serializable
enum class TaskState(val text: String){
    TODO("TODO"),
    STARTED("STARTED"),
    DONE("DONE")
}
@Serializable
enum class TaskCategory(val category: String) {
    WORKOUT("WORKOUT"),
    TRADING("TRADING"),
    RUNNING("RUNNING"),
    FOCUS("FOCUS"),
    HEALTH("HEALTH")
}