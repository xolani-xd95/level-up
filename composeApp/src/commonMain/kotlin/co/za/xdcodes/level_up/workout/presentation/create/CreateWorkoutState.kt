package co.za.xdcodes.level_up.workout.presentation.create

import co.za.xdcodes.level_up.workout.domain.dto.ExerciseCategory
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutExercise
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class CreateWorkoutState(
    val selectedCategories: List<ExerciseCategory> = emptyList(),
    val selectedExercises: List<WorkoutExercise> = emptyList(),
    val groupedExercises: List< Map<ExerciseCategory, List<WorkoutExercise>> > = emptyList(),
    val dayOfWeek: List<DayOfWeek> = emptyList(),
    val startDate: String = "",
    val durationWeeks: Int = 4,
    val workoutSchedule: List<LocalDate> = emptyList()
)

data class WeeklySchedule(
    val dayOfWeek: List<DayOfWeek> = emptyList(),
    val startDate: String = "",
    val durationWeeks: Int = 4
)