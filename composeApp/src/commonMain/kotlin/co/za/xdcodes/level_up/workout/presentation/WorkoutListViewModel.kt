package co.za.xdcodes.level_up.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import co.za.xdcodes.level_up.workout.domain.WorkoutRepository
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutSetModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class WorkoutListViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {
    private val _state = MutableStateFlow(WorkoutListState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val timeZone = TimeZone.currentSystemDefault()
            val currentDate = Clock.System.todayIn(timeZone)
            val workouts = repository.getWorkoutForToday(currentDate.toString())

            _state.update {
                it.copy(
                    listOfWorkouts = workouts
                )
            }
        }
    }

    fun onAction(action: WorkoutListAction) {
        when (action) {
            is WorkoutListAction.OnWorkoutComplete -> {
                completeWorkoutSet(action.workoutId, action.set)

                _state.update {
                    it.copy(
                        listOfWorkouts = state.value.listOfWorkouts.map { workout ->
                            if (workout.workoutId != action.workoutId) return@map workout

                            workout.copy(
                                sets = workout.sets.mapIndexed { index, set ->
                                    if (index + 1 == action.set.setNumber) {
                                        set.copy(
                                            weight = action.set.weight,
                                            reps = action.set.reps,
                                            isComplete = action.set.isComplete
                                        )
                                    } else set
                                }
                            )
                        }
                    )
                }
            }

            is WorkoutListAction.OnDateChanged -> {}
        }
    }

    fun completeWorkoutSet(workoutId: String, set: WorkoutSetModel) {
        val timeZone = TimeZone.currentSystemDefault()
        val currentDate = Clock.System.todayIn(timeZone)

        viewModelScope.launch {
            repository.completeWorkoutSet(currentDate.toString(), workoutId, set)
        }
    }
}