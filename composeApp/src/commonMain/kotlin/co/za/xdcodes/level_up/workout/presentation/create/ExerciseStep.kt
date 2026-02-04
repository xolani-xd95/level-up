package co.za.xdcodes.level_up.workout.presentation.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.theme.LevelUpTheme
import co.za.xdcodes.level_up.workout.domain.dto.ExerciseCategory
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutExercise
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ExerciseStep(
    viewModel: CreateWorkoutViewModel = koinViewModel<CreateWorkoutViewModel>()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getExercises()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(state.groupedExercises.size) { index ->
            val categoryExercise = state.groupedExercises[index]
            val category = categoryExercise.keys.first()
            val exercises = categoryExercise.getValue(category)
            val selectedExercises = state.selectedExercises

            ExerciseCategoryCard(
                category = category,
                exercises = exercises,
                selectedExercises = selectedExercises,
                onExerciseToggle = { viewModel.addExercises(it) },
            )
        }
    }
}

@Composable
fun ExerciseCategoryCard(
    category: ExerciseCategory,
    exercises: List<WorkoutExercise>,
    selectedExercises: List<WorkoutExercise>,
    onExerciseToggle: (WorkoutExercise) -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                category.name,
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
            )

            Icon(
                imageVector = if (isExpanded)
                    Icons.Outlined.KeyboardArrowUp
                else
                    Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.05f))
            ) {
                exercises.forEach { workout ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp)
                            .clickable { onExerciseToggle(workout) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            workout.name,
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                        )

                        Checkbox(
                            checked = selectedExercises.contains(workout),
                            onCheckedChange = {
                                onExerciseToggle(workout)
                            }
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                }
            }
        }
    }
}

@Preview
@Composable
fun ExerciseStepPreivew() {
    LevelUpTheme {
        val groupedState = listOf(
            mapOf(
                ExerciseCategory.FOREARMS to listOf(
                    WorkoutExercise(
                        "",
                        "Bicep Curl",
                        ExerciseCategory.BICEPS,
                        3,
                        ""
                    )
                )
            ),
            mapOf(
                ExerciseCategory.BICEPS to listOf(
                    WorkoutExercise(
                        "",
                        "Tricep Curl",
                        ExerciseCategory.BICEPS,
                        3,
                        ""
                    )
                )
            ),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(groupedState.size) { index ->
                val categoryExercise = groupedState[index]
                val category = categoryExercise.keys.first()
                val exercises = categoryExercise.getValue(category)


                ExerciseCategoryCard(
                    category = category,
                    exercises = exercises,
                    selectedExercises = emptyList(),
                    onExerciseToggle = {},
                )
            }
        }
    }
}