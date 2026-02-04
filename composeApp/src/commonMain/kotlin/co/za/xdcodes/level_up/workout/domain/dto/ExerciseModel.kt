package co.za.xdcodes.level_up.workout.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseModel(
    val category: ExerciseCategory,
    val name: String,
    val equipment: String
)