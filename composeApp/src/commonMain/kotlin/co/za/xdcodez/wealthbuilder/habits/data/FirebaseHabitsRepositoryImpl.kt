package co.za.xdcodez.wealthbuilder.habits.data

import co.za.xdcodez.wealthbuilder.habits.domain.HabitsRepository
import co.za.xdcodez.wealthbuilder.habits.domain.model.GoalType
import co.za.xdcodez.wealthbuilder.habits.domain.model.HabitKey
import co.za.xdcodez.wealthbuilder.habits.domain.model.Quarter
import co.za.xdcodez.wealthbuilder.habits.domain.model.QuarterlyGoal
import co.za.xdcodez.wealthbuilder.habits.domain.model.WeeklyRollup
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class FirebaseHabitsRepositoryImpl(
    private val firestore: FirebaseFirestore
) : HabitsRepository {

    // ── Quarter Data ─────────────────────────────────────────────

    override suspend fun getQuarter(quarter: String): Quarter? {
        return try {
            val snapshot = firestore
                .collection("quarters")
                .document(quarter)
                .get()

            if (!snapshot.exists) return null

            Quarter(
                id = quarter,
                startDate = snapshot.get<String?>("startDate") ?: "",
                endDate = snapshot.get<String?>("endDate") ?: "",
                totalDays = snapshot.get<Int?>("totalDays") ?: 0,
                currentDayNumber = snapshot.get<Int?>("currentDayNumber") ?: 1,
                goalDaysCompleted = snapshot.get<Int?>("goalDaysCompleted") ?: 0,
                title = snapshot.get<String?>("title") ?: "DISCIPLINED OPERATOR"
            )
        } catch (e: Exception) {
            println("🔴 Error getting quarter: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateQuarterDayNumber(quarter: String, dayNumber: Int): Result<Unit> = try {
        val quarterRef = firestore
            .collection("quarters")
            .document(quarter)

        @Suppress("DEPRECATION")
        quarterRef.update(mapOf("currentDayNumber" to dayNumber))

        Result.success(Unit)
    } catch (e: Exception) {
        println("🔴 Error updating quarter day number: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    override suspend fun incrementGoalDaysCompleted(quarter: String): Result<Unit> = try {
        val quarterRef = firestore
            .collection("quarters")
            .document(quarter)

        // Get current value
        val snapshot = quarterRef.get()
        val currentCompleted = snapshot.get<Int?>("goalDaysCompleted") ?: 0
        val newCompleted = currentCompleted + 1

        // Update with new value
        @Suppress("DEPRECATION")
        quarterRef.update(mapOf("goalDaysCompleted" to newCompleted))

        Result.success(Unit)
    } catch (e: Exception) {
        println("🔴 Error incrementing goal days completed: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    // ── Quarterly Goals ──────────────────────────────────────────

    override suspend fun getQuarterlyGoals(quarter: String): List<QuarterlyGoal> {
        return try {
            val snapshot = firestore
                .collection("quarters")
                .document(quarter)
                .collection("goals")
                .get()

            snapshot.documents.mapNotNull { doc ->
                try {
                    // Read required fields
                    val habitKeyStr = doc.get<String?>("habitKey")
                    val titleStr = doc.get<String?>("title")
                    val typeStr = doc.get<String?>("type")
                    val quarterStr = doc.get<String?>("quarter")
                    val lastUpdatedAtStr = try { doc.get<String?>("lastUpdatedAt") } catch (e: Exception) { null }

                    // Read optional fields for METRIC goals
                    val targetValueDbl = doc.get<Double?>("targetValue")
                    val currentValueDbl = doc.get<Double?>("currentValue")
                    val weeklyTargetDbl = try { doc.get<Double?>("weeklyTarget") } catch (e: Exception) { null }

                    // Read optional fields for MILESTONE goals (use try-catch since field may not exist)
                    val dueDateStr = try { doc.get<String?>("dueDate") } catch (e: Exception) { null }
                    val isCompleteBool = try { doc.get<Boolean?>("isComplete") } catch (e: Exception) { null }

                    QuarterlyGoal(
                        id = doc.id,
                        habitKey = habitKeyStr?.let { HabitKey.valueOf(it) },
                        title = titleStr ?: "",
                        type = GoalType.valueOf(typeStr ?: return@mapNotNull null),
                        targetValue = targetValueDbl,
                        currentValue = currentValueDbl,
                        dueDate = dueDateStr?.let { LocalDate.parse(it) },
                        isComplete = isCompleteBool ?: false,
                        quarter = quarterStr ?: "",
                        lastUpdatedAt = lastUpdatedAtStr,
                        weeklyTarget = weeklyTargetDbl
                    )
                } catch (e: Exception) {
                    println("🔴 Error parsing goal document ${doc.id}: ${e.message}")
                    e.printStackTrace()
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun incrementGoalValue(
        quarter: String,
        goalId: String,
        incrementBy: Double
    ): Result<Unit> = try {
        val goalRef = firestore
            .collection("quarters")
            .document(quarter)
            .collection("goals")
            .document(goalId)

        // Get current value
        val snapshot = goalRef.get()
        val currentValue = snapshot.get<Double?>("currentValue") ?: 0.0
        val newValue = currentValue + incrementBy

        // Update with new value
        @Suppress("DEPRECATION")
        goalRef.update(mapOf("currentValue" to newValue))

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateGoalProgress(
        quarter: String,
        goalId: String,
        incrementValue: Double,
        lastUpdatedAt: String?
    ): Result<Unit> = try {
        val goalRef = firestore
            .collection("quarters")
            .document(quarter)
            .collection("goals")
            .document(goalId)

        // Get current value
        val snapshot = goalRef.get()
        val currentValue = snapshot.get<Double?>("currentValue") ?: 0.0
        val newValue = currentValue + incrementValue

        // Build update map
        val updates = mutableMapOf<String, Any?>(
            "currentValue" to newValue
        )

        // Add lastUpdatedAt if provided, or use FieldValue.delete() to remove it
        if (lastUpdatedAt != null) {
            updates["lastUpdatedAt"] = lastUpdatedAt
        }

        // Update Firebase
        @Suppress("DEPRECATION")
        goalRef.update(updates)

        Result.success(Unit)
    } catch (e: Exception) {
        println("🔴 Error updating goal progress: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    // ── Quarter Setup ────────────────────────────────────────────

    override suspend fun initializeQuarter(quarter: String): Result<Unit> = try {
        // Parse quarter to get dates (e.g., "2026-Q3")
        val (year, quarterNum) = quarter.split("-Q").let {
            it[0].toInt() to it[1].toInt()
        }

        // Get the next Monday as start date
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val nextMonday = getNextMonday(today)

        // Get quarter end date
        val (endMonth, endDay) = when (quarterNum) {
            1 -> 3 to 31  // Q1: Mar 31
            2 -> 6 to 30  // Q2: Jun 30
            3 -> 9 to 30  // Q3: Sep 30
            4 -> 12 to 31 // Q4: Dec 31
            else -> throw IllegalArgumentException("Invalid quarter number: $quarterNum")
        }

        val startDate = nextMonday
        val endDate = LocalDate(year, endMonth, endDay)
        val totalDays = (endDate.toEpochDays() - startDate.toEpochDays()).toInt() + 1
        val totalWeeks = totalDays / 7

        // Initialize quarters/{quarter} document
        firestore
            .collection("quarters")
            .document(quarter)
            .set(
                mapOf(
                    "startDate" to startDate.toString(),
                    "endDate" to endDate.toString(),
                    "totalDays" to totalDays,
                    "currentDayNumber" to 1,
                    "title" to "DISCIPLINED OPERATOR",
                    "goalDaysCompleted" to 0
                )
            )

        // Initialize default goals as subcollection: quarters/{quarter}/goals/{goalId}
        val quartersRef = firestore.collection("quarters").document(quarter).collection("goals")

        // Create all goals and await completion
        // Targets calculated dynamically based on weeks remaining
        println("🟢 Creating Impulse-Control goal...")
        quartersRef.document("impulse-control").set(
            mapOf(
                "habitKey" to HabitKey.IMPULSE_CONTROL.name,
                "title" to "Impulse-Control Days",
                "type" to GoalType.METRIC_MANUAL.name,
                "targetValue" to (7.0 * totalWeeks),  // 7/week × totalWeeks
                "currentValue" to 0.0,
                "weeklyTarget" to 7.0,
                "quarter" to quarter
            )
        )
        println("🟢 Impulse-Control goal created")

        println("🟢 Creating Gym goal...")
        quartersRef.document("gym").set(
            mapOf(
                "habitKey" to HabitKey.GYM.name,
                "title" to "Gym Sessions",
                "type" to GoalType.METRIC_MANUAL.name,
                "targetValue" to (4.0 * totalWeeks),  // 4/week × totalWeeks
                "currentValue" to 0.0,
                "weeklyTarget" to 4.0,
                "quarter" to quarter
            )
        )
        println("🟢 Gym goal created")

        println("🟢 Creating Focus goal...")
        quartersRef.document("focus").set(
            mapOf(
                "habitKey" to HabitKey.FOCUS.name,
                "title" to "Focus Wins",
                "type" to GoalType.METRIC_MANUAL.name,
                "targetValue" to (5.0 * totalWeeks),  // 5/week × totalWeeks
                "currentValue" to 0.0,
                "weeklyTarget" to 5.0,
                "quarter" to quarter
            )
        )
        println("🟢 Focus goal created")

        println("🟢 Creating Savings goal...")
        quartersRef.document("quarter-savings").set(
            mapOf(
                "title" to "Quarterly Savings",
                "type" to GoalType.SAVINGS.name,
                "targetValue" to 60000.0,
                "currentValue" to 5000.0,
                "quarter" to quarter
            )
        )
        println("🟢 Savings goal created")

        println("🟢 Creating Savings goal...")
        quartersRef.document("sphosh-birthday-savings").set(
            mapOf(
                "title" to "Sphosh Birthday Savings",
                "type" to GoalType.SAVINGS.name,
                "targetValue" to 5000.0,
                "currentValue" to 5000.0,
                "quarter" to quarter
            )
        )
        println("🟢 Savings goal created")

        println("🟢 Creating Cardio goal...")
        quartersRef.document("cardio").set(
            mapOf(
                "habitKey" to HabitKey.CARDIO.name,
                "title" to "Cardio Sessions",
                "type" to GoalType.METRIC_MANUAL.name,
                "targetValue" to (4.0 * totalWeeks),  // 4/week × totalWeeks
                "currentValue" to 0.0,
                "weeklyTarget" to 4.0,
                "quarter" to quarter
            )
        )
        println("🟢 cardio goal created")

        println("🟢 All goals created successfully")
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addMissingGoalsToQuarter(quarter: String): Result<Unit> {
        return try {
            // Get existing goals
            val existingGoals = getQuarterlyGoals(quarter)
            val existingHabitKeys = existingGoals.mapNotNull { it.habitKey }.toSet()

            // Get quarter data to calculate weeks remaining
            val quarterData = getQuarter(quarter)
            if (quarterData == null) {
                println("🔴 Quarter $quarter not found")
                return Result.failure(Exception("Quarter not found"))
            }

            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val endDate = LocalDate.parse(quarterData.endDate)
            val daysRemaining = (endDate.toEpochDays() - today.toEpochDays()).toInt()
            val weeksRemaining = (daysRemaining / 7).coerceAtLeast(1)

            val goalsRef = firestore
                .collection("quarters")
                .document(quarter)
                .collection("goals")

            // Add Cardio goal if missing
            if (HabitKey.CARDIO !in existingHabitKeys) {
                println("🟢 Adding Cardio goal to $quarter...")
                goalsRef.document("cardio").set(
                    mapOf(
                        "habitKey" to HabitKey.CARDIO.name,
                        "title" to "Cardio Session Wins",
                        "type" to GoalType.METRIC_MANUAL.name,
                        "targetValue" to (4.0 * weeksRemaining),  // 4/week × weeksRemaining
                        "currentValue" to 0.0,
                        "weeklyTarget" to 4.0,
                        "quarter" to quarter
                    )
                )
                println("🟢 Cardio goal added")
            } else {
                println("🟡 Cardio goal already exists in $quarter")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            println("🔴 Error adding missing goals: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // ── Weekly Rollups ───────────────────────────────────────────

    override suspend fun getWeeklyRollup(quarter: String, weekStart: String): WeeklyRollup? {
        return try {
            val snapshot = firestore
                .collection("quarters")
                .document(quarter)
                .collection("weeklyRollups")
                .document(weekStart)
                .get()

            if (!snapshot.exists) return null

            // Parse dailyFocusBlocks map
            val dailyFocusBlocksRaw = try {
                snapshot.get<Map<String, Int>>("dailyFocusBlocks") ?: emptyMap()
            } catch (e: Exception) {
                emptyMap()
            }

            WeeklyRollup(
                weekStart = LocalDate.parse(snapshot.get<String>("weekStart") ?: return null),
                weekEnd = LocalDate.parse(snapshot.get<String>("weekEnd") ?: return null),
                weekNumber = snapshot.get<Int>("weekNumber") ?: 1,
                impulseControlDays = snapshot.get<Int>("impulseControlDays") ?: 0,
                cardioDays = snapshot.get<Int>("cardioDays") ?: 0,
                gymDays = snapshot.get<Int>("gymDays") ?: 0,
                focusDays = snapshot.get<Int>("focusDays") ?: 0,
                dailyFocusBlocks = dailyFocusBlocksRaw
            )
        } catch (e: Exception) {
            println("🔴 Error getting weekly rollup: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    override suspend fun getCurrentWeekRollup(quarter: String): WeeklyRollup? {
        return try {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val weekStart = getWeekStart(today)
            getWeeklyRollup(quarter, weekStart.toString())
        } catch (e: Exception) {
            println("🔴 Error getting current week rollup: ${e.message}")
            null
        }
    }

    override suspend fun updateWeeklyRollup(
        quarter: String,
        weekStart: String,
        habitKey: HabitKey,
        date: String,
        completed: Boolean,
        focusBlocks: Int?
    ): Result<Unit> = try {
        val weekDocRef = firestore
            .collection("quarters")
            .document(quarter)
            .collection("weeklyRollups")
            .document(weekStart)

        // Get current rollup or create new
        val snapshot = weekDocRef.get()

        if (!snapshot.exists) {
            // Initialize new week document
            val weekStartDate = LocalDate.parse(weekStart)
            val weekEndDate = LocalDate(weekStartDate.year, weekStartDate.monthNumber, weekStartDate.dayOfMonth + 6)
            val weekNum = calculateWeekNumber(quarter, weekStartDate)

            weekDocRef.set(
                mapOf(
                    "weekStart" to weekStart,
                    "weekEnd" to weekEndDate.toString(),
                    "weekNumber" to weekNum,
                    "impulseControlDays" to 0,
                    "cardioDays" to 0,
                    "gymDays" to 0,
                    "focusDays" to 0
                )
            )
        }

        // Re-fetch to get latest data
        val currentSnapshot = weekDocRef.get()

        // Get current counter values
        val currentImpulse = currentSnapshot.get<Int>("impulseControlDays") ?: 0
        val currentGym = currentSnapshot.get<Int>("gymDays") ?: 0
        val currentCardio = currentSnapshot.get<Int>("cardioDays") ?: 0
        val currentFocus = currentSnapshot.get<Int>("focusDays") ?: 0

        // Build updates based on habitKey
        val updates = mutableMapOf<String, Any>()
        when (habitKey) {
            HabitKey.IMPULSE_CONTROL -> {
                if (completed) {
                    updates["impulseControlDays"] = currentImpulse + 1
                }
            }
            HabitKey.CARDIO -> {
                if (completed) {
                    updates["cardioDays"] = currentCardio + 1
                }
            }
            HabitKey.GYM -> {
                if (completed) {
                    updates["gymDays"] = currentGym + 1
                }
            }
            HabitKey.FOCUS -> {
                // Focus: Only increment if 3 blocks completed (completed flag means 3 blocks)
                if (completed) {
                    updates["focusDays"] = currentFocus + 1
                }
            }
        }

        if (updates.isNotEmpty()) {
            println("Error getting current week rollup: ${updates}")
            @Suppress("DEPRECATION")
            weekDocRef.update(updates)
        }

        Result.success(Unit)
    } catch (e: Exception) {
        println("🔴 Error updating weekly rollup: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    override suspend fun updateDailyFocusBlocks(
        quarter: String,
        weekStart: String,
        date: String,
        blocksCompleted: Int
    ): Result<Unit> = try {
        val weekDocRef = firestore
            .collection("quarters")
            .document(quarter)
            .collection("weeklyRollups")
            .document(weekStart)

        // Ensure the week document exists
        val snapshot = weekDocRef.get()
        if (!snapshot.exists) {
            // Initialize new week document
            val weekStartDate = LocalDate.parse(weekStart)
            val weekEndDate = LocalDate(weekStartDate.year, weekStartDate.monthNumber, weekStartDate.dayOfMonth + 6)
            val weekNum = calculateWeekNumber(quarter, weekStartDate)

            weekDocRef.set(
                mapOf(
                    "weekStart" to weekStart,
                    "weekEnd" to weekEndDate.toString(),
                    "weekNumber" to weekNum,
                    "impulseControlDays" to 0,
                    "gymDays" to 0,
                    "focusDays" to 0,
                    "dailyFocusBlocks" to mapOf(date to blocksCompleted)
                )
            )
        } else {
            // Update the dailyFocusBlocks map for this specific date
            @Suppress("DEPRECATION")
            weekDocRef.update(mapOf("dailyFocusBlocks.$date" to blocksCompleted))
        }

        Result.success(Unit)
    } catch (e: Exception) {
        println("🔴 Error updating daily focus blocks: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    // ── Helper Methods ───────────────────────────────────────────

    private fun getWeekStart(date: LocalDate): LocalDate {
        val dayOfWeek = date.dayOfWeek.ordinal  // Monday=0, Sunday=6
        return LocalDate(date.year, date.monthNumber, date.dayOfMonth - dayOfWeek)
    }

    private fun getNextMonday(date: LocalDate): LocalDate {
        val dayOfWeek = date.dayOfWeek.ordinal  // Monday=0, Sunday=6
        val daysUntilMonday = if (dayOfWeek == 0) 0 else (7 - dayOfWeek)
        return LocalDate(date.year, date.monthNumber, date.dayOfMonth + daysUntilMonday)
    }

    private fun calculateWeekNumber(quarter: String, weekStartDate: LocalDate): Int {
        val (year, quarterNum) = quarter.split("-Q").let {
            it[0].toInt() to it[1].toInt()
        }
        val quarterStartMonth = (quarterNum - 1) * 3 + 1
        val quarterStart = LocalDate(year, quarterStartMonth, 1)

        val daysSinceQuarterStart = (weekStartDate.toEpochDays() - quarterStart.toEpochDays()).toInt()
        return (daysSinceQuarterStart / 7) + 1
    }
}
