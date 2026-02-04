package co.za.xdcodes.level_up.workout.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutExercise(
    val exerciseId: String,
    val name: String,
    val category: ExerciseCategory,
    val sets: Int = 3,
    val equipment: String,
)

@Serializable
data class WorkoutWithSets(
    val workoutId: String,
    val name: String,
    val category: ExerciseCategory,
    val isCompleted: Boolean? = null,
    val sets: List<WorkoutSetModel>
)
