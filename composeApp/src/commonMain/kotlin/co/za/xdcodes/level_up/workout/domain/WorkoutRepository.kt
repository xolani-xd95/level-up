package co.za.xdcodes.level_up.workout.domain

import co.za.xdcodes.level_up.workout.domain.dto.WorkoutExercise
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutSetModel
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutWithSets
import kotlinx.datetime.LocalDate

interface WorkoutRepository {
    suspend fun getExerciseByMuscle(muscle: String): List<WorkoutExercise>
    suspend fun createWorkout(schedule: List<LocalDate>, exercises: List<WorkoutExercise>)

    suspend fun getWorkoutForToday(date: String): List<WorkoutWithSets>

    suspend fun completeWorkoutSet(date: String, workoutId: String, set: WorkoutSetModel)
}