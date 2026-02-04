package co.za.xdcodes.level_up.dashboard.data

import co.za.xdcodes.level_up.dashboard.domain.dto.DashboardModel
import co.za.xdcodes.level_up.dashboard.domain.DashboardRepository
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskCategory
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskModel
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskPriority
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskState
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FirebaseDashboardRepository(
    private val firestore: FirebaseFirestore
) : DashboardRepository {
    override suspend fun getDashboardStreak(): DashboardModel {
        val snapshot = firestore
            .collection("dashboard")
            .document("Q1")
            .get()

        return DashboardModel(
            goalDaysCompleted = snapshot.get("goalDaysCompleted") ?: 0,
            goalDays = snapshot.get("goalDays") ?: 0,
            workoutStreak = snapshot.get("workoutStreak") ?: 0,
            runningStreak = snapshot.get("runningStreak") ?: 0,
            tradingDiscipline = snapshot.get("tradingDiscipline") ?: 0,
            lastCheckInDate = snapshot.get("lastCheckInDate") ?: "",
        )
    }

    override suspend fun updateDashboardStreak(field: String, value: Int) {
        firestore
            .collection("dashboard")
            .document("Q1")
            .update(
                mapOf(field to value)
            )

    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addTask(task: TaskModel): Result<Unit> {
//        val listOfDates = listOf(
////            "2026-01-30",
//            "2026-02-03",
//            "2026-02-05",
//            "2026-02-10",
//            "2026-02-12",
//////            "2026-02-11",
////            "2026-02-13",
//        )
//        val tasks = listOf(
//            TaskModel(
//                id = Uuid.random().toString(),
//                content = "4 cups of coffee + 1 and half bottles of water",
//                duration = "15min",
//                category = TaskCategory.HEALTH,
//                date = "date",
//                state = TaskState.TODO,
//                priority = TaskPriority.MEDIUM
//            ),
//            TaskModel(
//                id = Uuid.random().toString(),
//                content = "trading YT lesson",
//                duration = "1hr",
//                category = TaskCategory.TRADING,
//                date = "date",
//                state = TaskState.TODO,
//                priority = TaskPriority.MEDIUM
//            ),
//            TaskModel(
//                id = Uuid.random().toString(),
//                content = "coding session pt1",
//                duration = "1hr30min",
//                category = TaskCategory.FOCUS,
//                date = "date",
//                state = TaskState.TODO,
//                priority = TaskPriority.HIGH
//            ),
//            TaskModel(
//                id = Uuid.random().toString(),
//                content = "read 5 - 10 pages",
//                duration = "30min",
//                category = TaskCategory.FOCUS,
//                date = "date",
//                state = TaskState.TODO,
//                priority = TaskPriority.MEDIUM
//            ),
//            TaskModel(
//                id = Uuid.random().toString(),
//                content = "coding session pt2",
//                duration = "1hr30min",
//                category = TaskCategory.FOCUS,
//                date = "date",
//                state = TaskState.TODO,
//                priority = TaskPriority.MEDIUM
//            ),
////            TaskModel(
////                id = Uuid.random().toString(),
////                content = "morning run",
////                duration = "45min",
////                category = TaskCategory.RUNNING,
////                date = "date",
////                state = TaskState.TODO,
////                priority = TaskPriority.HIGH
////            ),
//            TaskModel(
//                id = Uuid.random().toString(),
//                content = "workout/gym",
//                duration = "1hr30min",
//                category = TaskCategory.WORKOUT,
//                date = "date",
//                state = TaskState.TODO,
//                priority = TaskPriority.MEDIUM
//            ),
//        )
//        listOfDates.forEach { date ->
//            tasks.forEach {_task ->
//                val test = _task.copy(date = date)
//                firestore
//                    .collection("tasks")
//                    .document(test.date)
//                    .collection("items")
//                    .document(test.id)
//                    .set(test)
//            }
//
//        }
        return try {
            firestore
                .collection("tasks")
                .document(task.date)
                .collection("items")
                .document(task.id)
                .set(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTasks(date: String): List<TaskModel> {
        val snapshot = firestore
            .collection("tasks")
            .document(date)
            .collection("items")
            .get()

        return snapshot.documents.map { doc ->
            TaskModel(
                id = doc.id,
                content = doc.get("content") ?: "",
                category = doc.get("category") ?: TaskCategory.WORKOUT,
                duration = doc.get("duration") ?: "",
                date = doc.get("date") ?: "",
                state = doc.get("state") ?: TaskState.TODO,
                priority = doc.get("priority") ?: TaskPriority.LOW
            )
        }
    }

    override suspend fun actionTask(task: TaskModel) {
        firestore
            .collection("tasks")
            .document(task.date)
            .collection("items")
            .document(task.id)
            .update(
                mapOf("state" to task.state.name)
            )
    }
}