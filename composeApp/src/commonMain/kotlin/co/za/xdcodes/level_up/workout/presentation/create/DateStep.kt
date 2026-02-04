package co.za.xdcodes.level_up.workout.presentation.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.theme.LevelUpTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
fun DateStep(
    viewModel: CreateWorkoutViewModel = koinViewModel<CreateWorkoutViewModel>()
) {
    val state by viewModel.state.collectAsState()

    DateSelector(
        state = state, onToggleDay = { day ->
            viewModel.addWorkoutDay(day)
        }, onWeeksChanged = { weeks ->
            viewModel.updateDuration(weeks)
        }
    )
}

@Composable
fun DateSelector(
    state: CreateWorkoutState,
    onToggleDay: (DayOfWeek) -> Unit,
    onWeeksChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(start = 8.dp)
    ) {
        Text(
            "Training Days",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            (0..5).forEach { index ->
                val day = DayOfWeek.entries[index]
                FilterChip(
                    colors = FilterChipDefaults.filterChipColors()
                        .copy(selectedContainerColor = MaterialTheme.colorScheme.primary),
                    selected = state.dayOfWeek.contains(day),
                    onClick = { onToggleDay(day) },
                    label = {
                        Text(
                            day.name.take(3),
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }
                )
            }
        }

        Text(
            "Duration",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Slider(
            value = state.durationWeeks.toFloat(),
            onValueChange = { onWeeksChanged(it.toInt()) },
            valueRange = 1f..12f,
            steps = 10,
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.primary,
                thumbColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(
            "Routine will repeat for ${state.durationWeeks} week/s",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Preview
@Composable
fun DateStepPreview() {
    LevelUpTheme {
        DateSelector(state = CreateWorkoutState(), {}, {})
    }
}