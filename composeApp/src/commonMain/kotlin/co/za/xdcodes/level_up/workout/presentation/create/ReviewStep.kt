package co.za.xdcodes.level_up.workout.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun ReviewStep(
    viewModel: CreateWorkoutViewModel = koinViewModel<CreateWorkoutViewModel>()
) {
    val state by viewModel.state.collectAsState()
    val reviewExercises = state.selectedExercises.groupBy { exercise -> exercise.category }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Repeat consistently every ${state.dayOfWeek} for ${state.durationWeeks} week/s",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(reviewExercises.entries.toList(), key = { it.key.name }) { entry ->
                ExerciseReviewCard(
                    category = entry.key,
                    exercises = entry.value
                )
            }
        }
    }
}

@Composable
fun ExerciseReviewCard(
    category: ExerciseCategory,
    exercises: List<WorkoutExercise>,
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            category.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
        )

        Column(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.05f))
                .fillMaxWidth(),
        ) {
            exercises.forEach { workout ->
                Row(
                    modifier = Modifier.padding(start = 12.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        workout.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    )

                    Text(
                        "sets: ${workout.sets}",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            }
        }

    }
}


@Preview
@Composable
fun ReviewStepPreview() {
    LevelUpTheme {
        ReviewStep()
    }
}
