package co.za.xdcodes.level_up.workout.presentation

import co.za.xdcodes.level_up.workout.domain.dto.WorkoutSetModel
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutWithSets
import kotlin.uuid.ExperimentalUuidApi

data class WorkoutListState @OptIn(ExperimentalUuidApi::class) constructor(
    val isExpanded: Boolean = false,
    val isWorkoutComplete: Boolean = false,
    val selectedWorkoutDate: String = "",
    val listOfWorkouts: List<WorkoutWithSets> = emptyList()
)

sealed interface WorkoutListAction {
    data class OnWorkoutComplete(val workoutId: String, val set: WorkoutSetModel) : WorkoutListAction

    data class OnDateChanged(val date: String) : WorkoutListAction
}