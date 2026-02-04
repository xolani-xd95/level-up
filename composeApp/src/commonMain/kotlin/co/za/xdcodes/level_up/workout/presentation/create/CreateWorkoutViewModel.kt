package co.za.xdcodes.level_up.workout.presentation.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.za.xdcodes.level_up.workout.domain.WorkoutRepository
import co.za.xdcodes.level_up.workout.domain.dto.ExerciseCategory
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutExercise
import co.za.xdcodes.level_up.workout.presentation.WorkoutListAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

class CreateWorkoutViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CreateWorkoutState())
    val state = _state.asStateFlow()

    fun addCategory(category: ExerciseCategory) {
        _state.update { currentState ->
            val updateCategories = if (category in currentState.selectedCategories) {
                currentState.selectedCategories - category
            } else {
                currentState.selectedCategories + category
            }
            currentState.copy(
                groupedExercises = emptyList(),
                selectedExercises = emptyList(),
                selectedCategories = updateCategories.distinct()
            )
        }
    }

    fun addExercises(exercise: WorkoutExercise) {
        _state.update { currentState ->
            val updateExercises = if (exercise in currentState.selectedExercises) {
                currentState.selectedExercises - exercise
            } else {
                currentState.selectedExercises + exercise
            }
            currentState.copy(
                selectedExercises = updateExercises.distinct()
            )
        }
    }

    fun updateExerciseSet(id: String, set: Int) {
        _state.update {
            it.copy(
                selectedExercises = state.value.selectedExercises.map { exercise ->
                    if (exercise.exerciseId == id) {
                        exercise.copy(sets = set)
                    } else {
                        exercise
                    }
                }
            )
        }
    }

    fun addWorkoutDay(day: DayOfWeek) {
        _state.update {
            it.copy(
                dayOfWeek = state.value.dayOfWeek + day
            )
        }
    }

    fun updateDuration(weeks: Int) {
        _state.update {
            it.copy(
                durationWeeks = weeks
            )
        }
        generateWorkoutDates()
    }

    fun generateWorkoutDates() {

        val timeZone = TimeZone.currentSystemDefault()
        val currentDate = Clock.System.todayIn(timeZone)

        val endDate = currentDate.plus(DatePeriod(days = state.value.durationWeeks * 7))

        val result = mutableListOf<LocalDate>()

        var startDate = currentDate
        while (startDate <= endDate) {
            if (startDate.dayOfWeek in state.value.dayOfWeek) {
                result.add(startDate)
            }
            startDate = startDate.plus(DatePeriod(days = 1))
        }

        _state.update {
            it.copy(
                workoutSchedule = result
            )
        }
    }

    fun getExercises() {
        viewModelScope.launch {
            state.value.selectedCategories.forEach { category ->
                val exercises = repository.getExerciseByMuscle(category.name)
                val mapOfExercises = exercises.groupBy { exercise -> exercise.category }

                _state.update { currentState ->
                    val updatedList = currentState.groupedExercises + mapOfExercises
                    currentState.copy(
                        groupedExercises = updatedList.distinct()
                    )
                }
            }
        }
    }

    fun createWorkout() {
        viewModelScope.launch {
            repository.createWorkout(state.value.workoutSchedule, state.value.selectedExercises)
        }
    }
    fun onAction(action: WorkoutListAction) {
        when (action) {
            is WorkoutListAction.OnWorkoutComplete -> {
            }

            is WorkoutListAction.OnDateChanged -> {}
        }
    }
}