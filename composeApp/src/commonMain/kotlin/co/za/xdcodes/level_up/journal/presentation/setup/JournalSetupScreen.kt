package co.za.xdcodes.level_up.journal.presentation.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.common.formatCurrency
import co.za.xdcodes.level_up.common.toMonthName
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
fun JournalSetupScreenRoute(
    monthIndex: Int,
    year: Int,
    journalSetupViewModel: JournalSetupViewModel = koinViewModel<JournalSetupViewModel>(),
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        journalSetupViewModel.init(monthIndex, year)
        journalSetupViewModel.navigationEvent.collect { event ->
            when (event) {
                JournalSetupNavigationEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    val state by journalSetupViewModel.state.collectAsState()

    JournalSetupScreen(
        state = state,
        onAction = journalSetupViewModel::onAction,
        onNavigateBack = onNavigateBack
    )

}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun JournalSetupScreen(
    state: JournalSetupState,
    onAction: (JournalSetupActions) -> Unit,
    onNavigateBack: () -> Unit
) {
    val showNext = when {
        state.isFirstTimeSetup && state.currentStep == 1 -> state.isStep1Valid
        state.currentStep == state.totalSteps - 1 -> state.isStep2Valid
        state.currentStep == state.totalSteps -> true
        else -> state.isStep2Valid
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigateBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Edit category",
                    tint = Color.White
                )
            }
            Text(
                text = "Rules Setup",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(modifier = Modifier.size(44.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ── Step indicator ───────────────────────────────────
                Text(
                    text = "Step ${state.currentStep} of ${state.totalSteps}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0x99FFFFFF),
                    modifier = Modifier.padding(bottom = 18.dp)
                )

                // ── Step content ─────────────────────────────────────
                when {
                    state.isFirstTimeSetup && state.currentStep == 1 ->
                        TradingRulesContent(
                            state = state,
                            onAction = onAction,
                            modifier = Modifier.fillMaxSize()
                        )

                    state.currentStep == state.totalSteps - 1 ->
                        MonthlyTargetContent(
                            state = state,
                            onAction = onAction,
                            modifier = Modifier.fillMaxSize()
                        )

                    state.currentStep == state.totalSteps ->
                        ReviewSetupContent(
                            state = state,
                            modifier = Modifier.fillMaxSize()
                        )
                }
            }

            // ── Bottom navigation ────────────────────────────────────
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // back
                if (state.currentStep > 1) {
                    Row(
                        modifier = Modifier.clickable {
                            onAction(JournalSetupActions.PreviousStep)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Back",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(80.dp))
                }

                // next / save
                if (showNext) {
                    Row(
                        modifier = Modifier.clickable {
                            if (state.currentStep == state.totalSteps)
                                onAction(JournalSetupActions.SaveSetup)
                            else
                                onAction(JournalSetupActions.NextStep)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.currentStep == state.totalSteps) "Save" else "Next",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(80.dp))
                }
            }
        }
    }
}

@Composable
private fun TradingSessionsStep(
    session1Start: String,
    session1End: String,
    session2Start: String,
    session2End: String,
    onSession1StartChange: (String) -> Unit,
    onSession1EndChange: (String) -> Unit,
    onSession2StartChange: (String) -> Unit,
    onSession2EndChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Trading Sessions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Define your trading hours. Only trade during these sessions for discipline.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Session 1
        Text(
            text = "Session 1",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = session1Start,
                onValueChange = onSession1StartChange,
                label = { Text("Start Time") },
                placeholder = { Text("09:00") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            OutlinedTextField(
                value = session1End,
                onValueChange = onSession1EndChange,
                label = { Text("End Time") },
                placeholder = { Text("12:00") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Session 2
        Text(
            text = "Session 2",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = session2Start,
                onValueChange = onSession2StartChange,
                label = { Text("Start Time") },
                placeholder = { Text("15:00") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            OutlinedTextField(
                value = session2End,
                onValueChange = onSession2EndChange,
                label = { Text("End Time") },
                placeholder = { Text("16:30") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Time Format: HH:MM (24-hour)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview
@Composable
fun JournalSetupScreenPreview() {
    LevelUpTheme {
        JournalSetupScreen(
            state = JournalSetupState(),
            onAction = {},
            onNavigateBack = {}
        )
    }
}
