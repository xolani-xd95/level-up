package co.za.xdcodez.wealthbuilder.finance.data

import co.za.xdcodez.wealthbuilder.finance.domain.GoalRepository
import co.za.xdcodez.wealthbuilder.finance.domain.dto.Goal
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class FirebaseGoalRepositoryImpl(
    private val firestore: FirebaseFirestore
) : GoalRepository {

    private val goalsCollection = firestore.collection("goals")

    override suspend fun getGoal(goalId: String): Goal? {
        return try {
            val snapshot = goalsCollection.document(goalId).get()
            if (!snapshot.exists) return null

            Goal(
                id = goalId,
                name = snapshot.get("name") ?: "",
                targetAmount = snapshot.get("targetAmount") ?: 0.0,
                currentAmount = snapshot.get("currentAmount") ?: 0.0,
                startDate = snapshot.get("startDate") ?: "",
                endDate = snapshot.get("endDate") ?: "",
                linkedCategoryId = snapshot.get("linkedCategoryId"),
                createdAt = snapshot.get("createdAt") ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    override fun getAllGoals(): Flow<List<Goal>> {
        return goalsCollection.snapshots.map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                try {
                    Goal(
                        id = doc.id,
                        name = doc.get("name") ?: "",
                        targetAmount = doc.get("targetAmount") ?: 0.0,
                        currentAmount = doc.get("currentAmount") ?: 0.0,
                        startDate = doc.get("startDate") ?: "",
                        endDate = doc.get("endDate") ?: "",
                        linkedCategoryId = doc.get("linkedCategoryId"),
                        createdAt = doc.get("createdAt") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.createdAt }
        }
    }

    override fun getActiveGoals(): Flow<List<Goal>> {
        return getAllGoals().map { goals ->
            goals.filter { !it.isCompleted }
        }
    }

    override fun getCompletedGoals(): Flow<List<Goal>> {
        return getAllGoals().map { goals ->
            goals.filter { it.isCompleted }
        }
    }

    override suspend fun createGoal(goal: Goal): Result<Unit> = try {
        val createdAt = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

        goalsCollection.document(goal.id).set(
            mapOf(
                "name" to goal.name,
                "targetAmount" to goal.targetAmount,
                "currentAmount" to goal.currentAmount,
                "startDate" to goal.startDate,
                "endDate" to goal.endDate,
                "linkedCategoryId" to goal.linkedCategoryId,
                "createdAt" to createdAt
            )
        )
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateGoal(goal: Goal): Result<Unit> = try {
        goalsCollection.document(goal.id).update(
            mapOf(
                "name" to goal.name,
                "targetAmount" to goal.targetAmount,
                "currentAmount" to goal.currentAmount,
                "startDate" to goal.startDate,
                "endDate" to goal.endDate,
                "linkedCategoryId" to goal.linkedCategoryId
            )
        )
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteGoal(goalId: String): Result<Unit> = try {
        goalsCollection.document(goalId).delete()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateGoalProgress(goalId: String, amount: Double): Result<Unit> = try {
        val goal = getGoal(goalId) ?: return Result.failure(Exception("Goal not found"))
        val newAmount = (goal.currentAmount + amount).coerceAtLeast(0.0)

        goalsCollection.document(goalId).update(
            mapOf("currentAmount" to newAmount)
        )
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
