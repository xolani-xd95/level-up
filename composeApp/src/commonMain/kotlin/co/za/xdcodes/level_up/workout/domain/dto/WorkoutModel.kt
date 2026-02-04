package co.za.xdcodes.level_up.workout.domain.dto

import kotlinx.serialization.Serializable
@Serializable
data class WorkoutSetModel(
    val id: String = "",
    val setNumber: Int,
    val weight: Int,
    val reps: Int,
    val isComplete: Boolean? = null
)

@Serializable
enum class ExerciseCategory {
    BACK,
    BICEPS,
    CARDIO,
    CHEST,
    CORE,
    FOREARMS,
    FULLBODY,
    LEGS,
    SHOULDERS,
    TRICEPS
}

enum class CreateWorkoutStep(val title: String) {
    CATEGORY("Category"),
    EXERCISES("Exercises"),
    SETS("Sets"),
    DATE("Date"),
    REVIEW("Review")
}