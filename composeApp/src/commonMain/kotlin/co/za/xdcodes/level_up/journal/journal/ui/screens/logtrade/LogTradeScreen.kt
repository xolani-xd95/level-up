package com.trading.journal.ui.screens.logtrade

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trading.journal.domain.model.AssetClass
import com.trading.journal.domain.model.TradeDirection
import com.trading.journal.ui.components.SectionHeader
import com.trading.journal.ui.components.ThinDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogTradeScreen(
    viewModel: LogTradeViewModel,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text(
                "Log trade",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 28.sp,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        // ── Instrument ──
        item {
            SectionHeader("Instrument")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.symbol,
                onValueChange = { viewModel.update { copy(symbol = it) } },
                label = { Text("Symbol") },
                placeholder = { Text("AAPL, BTC, EUR/USD…") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.errors.containsKey("symbol"),
                supportingText = state.errors["symbol"]?.let { { Text(it) } },
                singleLine = true,
            )
            Spacer(Modifier.height(10.dp))
            // Direction toggle
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TradeDirection.entries.forEach { dir ->
                    FilterChip(
                        selected = state.direction == dir,
                        onClick = { viewModel.update { copy(direction = dir) } },
                        label = { Text(dir.name) },
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            // Asset class
            var acExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = acExpanded, onExpandedChange = { acExpanded = it }) {
                OutlinedTextField(
                    value = state.assetClass.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Asset class") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = acExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                ExposedDropdownMenu(expanded = acExpanded, onDismissRequest = { acExpanded = false }) {
                    AssetClass.entries.forEach { ac ->
                        DropdownMenuItem(
                            text = { Text(ac.name) },
                            onClick = { viewModel.update { copy(assetClass = ac) }; acExpanded = false },
                        )
                    }
                }
            }
        }

        item { ThinDivider() }

        // ── Prices ──
        item {
            SectionHeader("Pricing")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.entryPrice,
                    onValueChange = { viewModel.update { copy(entryPrice = it) } },
                    label = { Text("Entry price") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = state.errors.containsKey("entry"),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.exitPrice,
                    onValueChange = { viewModel.update { copy(exitPrice = it) } },
                    label = { Text("Exit price") },
                    placeholder = { Text("Leave blank = open") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.quantity,
                    onValueChange = { viewModel.update { copy(quantity = it) } },
                    label = { Text("Quantity") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.dateText,
                    onValueChange = { viewModel.update { copy(dateText = it) } },
                    label = { Text("Date (YYYY-MM-DD)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }
        }

        item { ThinDivider() }

        // ── Risk management ──
        item {
            SectionHeader("Risk management")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.stopLoss,
                    onValueChange = { viewModel.update { copy(stopLoss = it) } },
                    label = { Text("Stop loss") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.takeProfit,
                    onValueChange = { viewModel.update { copy(takeProfit = it) } },
                    label = { Text("Take profit") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
            }
        }

        item { ThinDivider() }

        // ── Journal ──
        item {
            SectionHeader("Journal")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.strategy,
                onValueChange = { viewModel.update { copy(strategy = it) } },
                label = { Text("Strategy / setup") },
                placeholder = { Text("e.g. Breakout, Mean reversion…") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.update { copy(notes = it) } },
                label = { Text("Notes / rationale") },
                placeholder = { Text("Why did you enter? What did you see?") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 6,
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.tagsText,
                onValueChange = { viewModel.update { copy(tagsText = it) } },
                label = { Text("Tags (comma separated)") },
                placeholder = { Text("earnings, trend, FOMO…") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }

        // ── Save button ──
        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    "Save trade",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                )
            }
        }
    }
}
