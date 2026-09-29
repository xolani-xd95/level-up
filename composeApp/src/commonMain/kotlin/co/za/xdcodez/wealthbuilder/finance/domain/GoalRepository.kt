package co.za.xdcodez.wealthbuilder.finance.domain

import co.za.xdcodez.wealthbuilder.finance.domain.dto.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    // ── Goals ────────────────────────────────────────────────────
    suspend fun getGoal(goalId: String): Goal?
    fun getAllGoals(): Flow<List<Goal>>
    fun getActiveGoals(): Flow<List<Goal>>
    fun getCompletedGoals(): Flow<List<Goal>>
    suspend fun createGoal(goal: Goal): Result<Unit>
    suspend fun updateGoal(goal: Goal): Result<Unit>
    suspend fun deleteGoal(goalId: String): Result<Unit>
    suspend fun updateGoalProgress(goalId: String, amount: Double): Result<Unit>
}
